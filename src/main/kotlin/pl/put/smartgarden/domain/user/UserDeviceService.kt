package pl.put.smartgarden.domain.user

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.put.smartgarden.domain.SmartGardenException
import pl.put.smartgarden.domain.device.Device
import pl.put.smartgarden.domain.device.SensorType
import pl.put.smartgarden.domain.device.repository.AreaRepository
import pl.put.smartgarden.domain.device.repository.DeviceRepository
import pl.put.smartgarden.domain.device.repository.SensorRepository
import pl.put.smartgarden.domain.user.dto.request.IrrigationLevelRequest
import pl.put.smartgarden.domain.user.dto.request.LocationRequest
import pl.put.smartgarden.domain.user.dto.request.NextIrrigationRequest
import pl.put.smartgarden.domain.user.dto.response.AreaResponse
import pl.put.smartgarden.domain.user.dto.response.AreaSensorMeasuresResponse
import pl.put.smartgarden.domain.user.dto.response.AreaSettingsResponse
import pl.put.smartgarden.domain.user.dto.response.MeasureMeasuresResponse
import pl.put.smartgarden.domain.user.dto.response.MeasureResponse
import pl.put.smartgarden.domain.user.dto.response.SensorResponse
import pl.put.smartgarden.domain.user.dto.response.SimpleAreaResponse
import pl.put.smartgarden.domain.user.dto.response.SimpleAreaSensorResponse
import pl.put.smartgarden.domain.user.dto.response.UserGeneralSettingsResponse
import pl.put.smartgarden.domain.user.repository.UserRepository
import java.time.Instant

@Transactional
@Service
class UserDeviceService(
    val deviceRepository: DeviceRepository,
    val userRepository: UserRepository,
    val areaRepository: AreaRepository,
    val sensorRepository: SensorRepository
) {

    fun createAndSaveDevice(deviceGuid: String, latitude: Double, longitude: Double, userId: Int): Device {
        if (deviceRepository.existsByGuid(deviceGuid))
            throw SmartGardenException("Device is in use by another account.", HttpStatus.BAD_REQUEST)
        val device = Device(
            guid = deviceGuid,
            latitude = latitude,
            longitude = longitude,
            userId = userId
        )

        return deviceRepository.save(device)
    }

    fun saveDevice(device: Device): Device = deviceRepository.save(device)

    fun setDeviceLocation(userId: Int, locationRequest: LocationRequest): UserGeneralSettingsResponse {
        val user = getUserById(userId)

        val device = user.device
        device?.latitude = locationRequest.latitude
        device?.longitude = locationRequest.longitude

        val updatedDevice = saveDevice(device!!)

        return UserGeneralSettingsResponse(
            username = user.username,
            email = user.email,
            deviceGuid = updatedDevice.guid,
            longitude = updatedDevice.longitude,
            latitude = updatedDevice.latitude
        )
    }

    fun getAreaMeasures(userId: Int, areaId: Int, from: Instant?, to: Instant?): List<MeasureResponse> {
        if (from != null && to != null && from.isAfter(to)) throw SmartGardenException("From cannot be after to!", HttpStatus.BAD_REQUEST)

        val device = getUserById(userId).device!!

        val areaFound = device.areas.firstOrNull { a -> a.id == areaId }
            ?: throw SmartGardenException("Can't find area with given id: $areaId", HttpStatus.BAD_REQUEST)

        return areaFound.sensors
            .filter { s -> s.isActive }
            .map { s ->
                MeasureResponse(s.guid, s.type.name, s.type.unit, s.measures
                    .filter { m -> (from == null || m.timestamp.isAfter(from)) && (to == null || m.timestamp.isBefore(to)) }
                    .map { m -> MeasureMeasuresResponse(m.timestamp, m.value) })
            }
    }

    fun setIrrigationLevel(userId: Int, areaId: String, irrigationLevelRequest: IrrigationLevelRequest): AreaSettingsResponse {
        TODO("Not yet implemented")
    }

    fun getAreasSetting(userId: Int): List<AreaSettingsResponse> {
        TODO("Not yet implemented")
    }

    fun setNextIrrigationTime(userId: Int, areaId: String, irrigationTimeRequest: NextIrrigationRequest): NextIrrigationRequest {
        TODO("Not yet implemented")
    }

    fun irrigateArea(userId: Int, areaId: String) {
        TODO("Not yet implemented")
    }

    fun linkSensorToArea(userId: Int, areaId: Int, sensorGuid: String): List<SimpleAreaResponse> {
        val user = getUserById(userId)
        val device = user.device!!
        val sensor = device.sensors.first { sensor -> sensor.guid == sensorGuid }
        val area = device.areas.first { area -> area.id == areaId }

        sensor.areaId = area.id
        if (area.sensors.firstOrNull { s -> sensorGuid == s.guid } == null)
        {
            area.sensors.add(sensor)

            sensorRepository.saveAndFlush(sensor)
            areaRepository.saveAndFlush(area)
        }

        return getAreasInfo(userId)
    }

    fun unlinkSensorFromArea(userId: Int, sensorGuid: String): List<SimpleAreaResponse> {
        val user = getUserById(userId)
        val device = user.device!!
        if (device.sensors.firstOrNull { sensor -> sensor.guid == sensorGuid } == null)
        {
            throw SmartGardenException("There is no sensor with such guid", HttpStatus.BAD_REQUEST)
        }

        val sensor = sensorRepository.findByGuid(sensorGuid).orElseThrow { SmartGardenException("There is no sensor with such guid", HttpStatus.BAD_REQUEST) }
        sensor.areaId = null
        sensorRepository.saveAndFlush(sensor)

        return getAreasInfo(userId)
    }

    fun getAllSensors(userId: Int, active: Boolean?): List<SensorResponse> {
        val device = getUserById(userId).device!!

        return device.sensors
            .filter { s -> active == null || s.isActive == active }
            .map { s -> SensorResponse(s.guid, s.type.name, s.type.unit, s.areaId, s.isActive) }
    }

    fun getAllAreasMeasures(userId: Int): List<AreaResponse> {
        val user = getUserById(userId)
        val areas = user.device?.areas!!
        val result = mutableListOf<AreaResponse>()

        for (area in areas) {
            val humidityMeasures = area.sensors.filter { sensor -> sensor.isActive && sensor.type == SensorType.HUMIDITY }.flatMap { sensor -> sensor.measures }
            val avgHumidity = area.sensors
                .filter { sensor -> sensor.isActive && sensor.type == SensorType.HUMIDITY }
                .map { sensor -> sensor.measures[sensor.measures.lastIndex].value }
                .average()
                .toInt()

            val illuminanceMeasures = area.sensors.filter { sensor -> sensor.isActive && sensor.type == SensorType.ILLUMINANCE }.flatMap { sensor -> sensor.measures }
            val avgIlluminance = area.sensors
                .filter { sensor -> sensor.isActive && sensor.type == SensorType.ILLUMINANCE }
                .map { sensor -> sensor.measures[sensor.measures.lastIndex].value }
                .average()
                .toInt()

            val temperatureMeasures = area.sensors.filter { sensor -> sensor.isActive && sensor.type == SensorType.TEMPERATURE }.flatMap { sensor -> sensor.measures }
            val avgTemperature = area.sensors
                .filter { sensor -> sensor.isActive && sensor.type == SensorType.TEMPERATURE }
                .map { sensor -> sensor.measures[sensor.measures.lastIndex].value }
                .average()
                .toInt()

            result.add(AreaResponse(
                id = area.id,
                humidity = avgHumidity,
                temperature = avgTemperature,
                illuminance = avgIlluminance,
                nextWateringTime = Instant.now(), // TODO
                humidityMeasures = humidityMeasures.map { measure -> AreaSensorMeasuresResponse(measure.timestamp, measure.value) },
                illuminanceMeasures = illuminanceMeasures.map { measure -> AreaSensorMeasuresResponse(measure.timestamp, measure.value) },
                temperatureMeasures = temperatureMeasures.map { measure -> AreaSensorMeasuresResponse(measure.timestamp, measure.value) }
            ))
        }

        return result
    }

    fun getAreasInfo(userId: Int): List<SimpleAreaResponse> {
        val result = mutableListOf<SimpleAreaResponse>()

        val areas = getUserById(userId).device!!.areas
        for (area in areas) {
            result.add(SimpleAreaResponse(area.id, area.sensors.map { s -> SimpleAreaSensorResponse(s.guid, s.type.name, s.type.unit, s.isActive) }))
        }

        return result
    }

    private fun getUserById(id: Int): User {
        val userOptional = userRepository.findById(id)

        if (!userOptional.isPresent) throw SmartGardenException("Invalid token", HttpStatus.UNAUTHORIZED)

        return userOptional.get()
    }
}