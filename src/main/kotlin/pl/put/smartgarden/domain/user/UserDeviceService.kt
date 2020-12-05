package pl.put.smartgarden.domain.user

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import pl.put.smartgarden.domain.SmartGardenException
import pl.put.smartgarden.domain.device.Device
import pl.put.smartgarden.domain.device.Measure
import pl.put.smartgarden.domain.device.SensorType
import pl.put.smartgarden.domain.device.dto.response.MeasureResponse
import pl.put.smartgarden.domain.device.dto.response.SensorResponse
import pl.put.smartgarden.domain.device.repository.DeviceRepository
import pl.put.smartgarden.domain.user.dto.request.IrrigationLevelRequest
import pl.put.smartgarden.domain.user.dto.request.LocationRequest
import pl.put.smartgarden.domain.user.dto.request.NextIrrigationRequest
import pl.put.smartgarden.domain.user.dto.response.AreaResponse
import pl.put.smartgarden.domain.user.dto.response.AreaSensorResponse
import pl.put.smartgarden.domain.user.dto.response.AreaSettingsResponse
import pl.put.smartgarden.domain.user.dto.response.UserGeneralSettingsResponse
import pl.put.smartgarden.domain.user.repository.UserRepository
import java.time.Instant

@Service
class UserDeviceService(
    val deviceRepository: DeviceRepository,
    val userRepository: UserRepository
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

    fun getAreaMeasures(userId: Int, areaId: String, from: Instant, to: Instant): List<MeasureResponse> {
        TODO("Not yet implemented")
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

    fun linkSensorToArea(userId: Int, areaId: String, sensorId: String): List<AreaResponse> {
        TODO("Not yet implemented")
    }

    fun unlinkSensorFromArea(userId: Int, sensorId: String): List<AreaResponse> {
        TODO("Not yet implemented")
    }

    fun getNotLinkedSensors(userId: Int): List<SensorResponse> {
        TODO("Not yet implemented")
    }

    fun getAvailableAreas(userId: Int): List<AreaResponse> {
        val user = getUserById(userId)
        val areas = user.device?.areas!!
        val result = mutableListOf<AreaResponse>()

        for (area in areas) {
            val humidityMeasures = area.sensors.filter { sensor -> sensor.type == SensorType.HUMIDITY }.flatMap { sensor -> sensor.measures }
            val avgHumidity = area.sensors
                .filter { sensor -> sensor.type == SensorType.HUMIDITY }
                .map { sensor -> sensor.measures[sensor.measures.lastIndex].value }
                .average()
                .toInt()

            val illuminanceMeasures = area.sensors.filter { sensor -> sensor.type == SensorType.ILLUMINANCE }.flatMap { sensor -> sensor.measures }
            val avgIlluminance = area.sensors
                .filter { sensor -> sensor.type == SensorType.ILLUMINANCE }
                .map { sensor -> sensor.measures[sensor.measures.lastIndex].value }
                .average()
                .toInt()

            val temperatureMeasures = area.sensors.filter { sensor -> sensor.type == SensorType.TEMPERATURE }.flatMap { sensor -> sensor.measures }
            val avgTemperature = area.sensors
                .filter { sensor -> sensor.type == SensorType.TEMPERATURE }
                .map { sensor -> sensor.measures[sensor.measures.lastIndex].value }
                .average()
                .toInt()

            result.add(AreaResponse(
                id = area.id,
                humidity = avgHumidity,
                temperature = avgTemperature,
                illuminance = avgIlluminance,
                nextWateringTime = Instant.now(), // TODO
                humidityMeasures = humidityMeasures.map { measure -> AreaSensorResponse(measure.timestamp, measure.value) },
                illuminanceMeasures = illuminanceMeasures.map { measure -> AreaSensorResponse(measure.timestamp, measure.value) },
                temperatureMeasures = temperatureMeasures.map { measure -> AreaSensorResponse(measure.timestamp, measure.value) }
            ))
        }

        return result
    }

    private fun getUserById(id: Int): User {
        val userOptional = userRepository.findById(id)

        if (!userOptional.isPresent) throw SmartGardenException("Invalid token", HttpStatus.UNAUTHORIZED)

        return userOptional.get()
    }
}