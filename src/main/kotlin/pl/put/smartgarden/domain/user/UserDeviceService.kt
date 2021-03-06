package pl.put.smartgarden.domain.user

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.put.smartgarden.domain.SmartGardenException
import pl.put.smartgarden.domain.device.Area
import pl.put.smartgarden.domain.device.AreaSettings
import pl.put.smartgarden.domain.device.Device
import pl.put.smartgarden.domain.device.Irrigation
import pl.put.smartgarden.domain.device.Sensor
import pl.put.smartgarden.domain.device.SensorType
import pl.put.smartgarden.domain.device.repository.AreaRepository
import pl.put.smartgarden.domain.device.repository.AreaSettingsRepository
import pl.put.smartgarden.domain.device.repository.DeviceRepository
import pl.put.smartgarden.domain.device.repository.IrrigationRepository
import pl.put.smartgarden.domain.device.repository.MeasureRepository
import pl.put.smartgarden.domain.device.repository.SensorRepository
import pl.put.smartgarden.domain.user.dto.request.AreaSettingsRequest
import pl.put.smartgarden.domain.user.dto.request.CreateAreaRequest
import pl.put.smartgarden.domain.user.dto.request.LinkSensorRequest
import pl.put.smartgarden.domain.user.dto.request.LocationRequest
import pl.put.smartgarden.domain.user.dto.request.SensorUpdateRequest
import pl.put.smartgarden.domain.user.dto.response.AreaIrrigationResponse
import pl.put.smartgarden.domain.user.dto.response.AreaMeasuresResponse
import pl.put.smartgarden.domain.user.dto.response.AreaSensorMeasuresResponse
import pl.put.smartgarden.domain.user.dto.response.AreaSettingsResponse
import pl.put.smartgarden.domain.user.dto.response.SensorResponse
import pl.put.smartgarden.domain.user.dto.response.SimpleAreaResponse
import pl.put.smartgarden.domain.user.dto.response.SimpleAreaSensorResponse
import pl.put.smartgarden.domain.user.dto.response.UserGeneralSettingsResponse
import pl.put.smartgarden.domain.user.repository.UserRepository
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

@Transactional
@Service
class UserDeviceService(
    val deviceRepository: DeviceRepository,
    val userRepository: UserRepository,
    val areaRepository: AreaRepository,
    val sensorRepository: SensorRepository,
    val settingsRepository: AreaSettingsRepository,
    val measureRepository: MeasureRepository,
    val irrigationRepository: IrrigationRepository
) {

    /** Creates and saves device. */
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

    /** Updates device location. */
    fun setDeviceLocation(userId: Int, locationRequest: LocationRequest): UserGeneralSettingsResponse {
        val user = getUserById(userId)
        val device = user.device!!

        device.latitude = locationRequest.latitude
        device.longitude = locationRequest.longitude

        val updatedDevice = deviceRepository.save(device)

        return UserGeneralSettingsResponse(
            username = user.username,
            email = user.email,
            deviceGuid = updatedDevice.guid,
            longitude = updatedDevice.longitude,
            latitude = updatedDevice.latitude
        )
    }

    /** Retrieves measures for given area. */
    fun getAreaMeasures(userId: Int, areaId: Int, dateFrom: Instant?, dateTo: Instant?): AreaMeasuresResponse {
        val device = getUserById(userId).device!!

        return getAreaMeasures(device, areaId, dateFrom, dateTo)
    }

    /** Retrieves measures for given area (only from active sensors). */
    private fun getAreaMeasures(device: Device, areaId: Int, dateFrom: Instant?, dateTo: Instant?): AreaMeasuresResponse {
        if (dateFrom != null && dateTo != null && dateFrom.isAfter(dateTo)) throw SmartGardenException("From cannot be after to!", HttpStatus.BAD_REQUEST)

        val area = device.areas.firstOrNull { a -> a.id == areaId }
            ?: throw SmartGardenException("Can't find area with given id: $areaId", HttpStatus.NOT_FOUND)

        var range: ChronoUnit
        var from = dateFrom
        var to = dateTo
        if (from == null && to == null)
        {
            from = Instant.now().truncatedTo(ChronoUnit.DAYS).minus(30, ChronoUnit.DAYS)
            to = Instant.now().truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS)
        }

        val seconds = Duration.between(from, to).seconds
        range = if (seconds <= 604800) {
            ChronoUnit.MINUTES
        } else {
            ChronoUnit.HOURS
        }

        val humidityPair = getMeasures(device, areaId, from, to, range, SensorType.HUMIDITY)
        val humidityMeasures = humidityPair.first
        var avgHumidity = humidityPair.second

        val temperaturePair = getMeasures(device, areaId, from, to, range, SensorType.TEMPERATURE)
        val temperatureMeasures = temperaturePair.first
        var avgTemperature = temperaturePair.second

        val illuminancePair = getMeasures(device, areaId, from, to, range, SensorType.ILLUMINANCE)
        val illuminanceMeasures = illuminancePair.first
        var avgIlluminance = illuminancePair.second

        val irrigations = irrigationRepository.getLastIrrigation(areaId)
        var nextWateringTime: Instant? = null
        if (irrigations.isNotEmpty()) {
            nextWateringTime = irrigations[0].timestamp.plusSeconds(1L * area.settings.frequencyValue * area.settings.frequencyUnit.inSeconds)
            if (nextWateringTime.isBefore(Instant.now()))
            {
                nextWateringTime = null
            }
        }

        return AreaMeasuresResponse(
            id = area.id,
            humidity = avgHumidity,
            temperature = avgTemperature,
            illuminance = avgIlluminance,
            nextWateringTime = nextWateringTime,
            settings = AreaSettingsResponse(
                area.settings.areaId!!,
                area.settings.frequencyUnit,
                area.settings.frequencyValue,
                area.settings.isIrrigationEnabled,
                area.settings.isWeatherEnabled,
                area.settings.strength,
                area.settings.threshhold
            ),
            sensors = area.sensors.map { s -> SimpleAreaSensorResponse(s.guid, s.type.name, s.type.unit, s.isActive) },
            freeSensors = sensorRepository.findAllByAreaIdAndDeviceId(null, device.id).map { s -> SimpleAreaSensorResponse(s.guid, s.type.name, s.type.unit, s.isActive) },
            humidityMeasures = humidityMeasures,
            illuminanceMeasures = illuminanceMeasures,
            temperatureMeasures = temperatureMeasures
        )
    }

    private fun getMeasures(device: Device, areaId: Int, from: Instant?, to: Instant?, range: ChronoUnit, sensorType: SensorType): Pair<List<AreaSensorMeasuresResponse>, Int> {
        val tmpMap = mutableMapOf<Instant, MutableList<Int>>()
        measureRepository.findMeasures(device.id, areaId, sensorType, from!!, to!!)
            .forEach { measure ->
                val timestamp = measure.timestamp.truncatedTo(range)
                if (tmpMap.containsKey(timestamp)) {
                    tmpMap[timestamp]?.add(measure.value)
                } else {
                    tmpMap[timestamp] = mutableListOf(measure.value)
                }
            }

        val measures = tmpMap
            .map { (k, v) -> AreaSensorMeasuresResponse(k, v.average().toInt()) }
            .sortedBy { m -> m.timestamp }
            .toList()

        var avgLastMeasure = 0
        if (measures.isNotEmpty()) {
            avgLastMeasure = measures[measures.size - 1].value
        }
        return Pair(measures, avgLastMeasure)
    }

    /** Retrieves settings of all areas. */
    fun getAreasSetting(userId: Int): List<AreaSettingsResponse> {
        val userById = getUserById(userId)
        val device = userById.device!!

        return device.areas.map { area ->
            AreaSettingsResponse(
                area.settings.areaId!!,
                area.settings.frequencyUnit,
                area.settings.frequencyValue,
                area.settings.isIrrigationEnabled,
                area.settings.isWeatherEnabled,
                area.settings.strength,
                area.settings.threshhold
            )
        }
    }

    /** Updates area settings. */
    fun setAreaSettings(userId: Int, areaId: Int, settings: AreaSettingsRequest): AreaSettingsResponse {
        val userById = getUserById(userId)
        val device = userById.device!!
        val area = device.areas.firstOrNull { area -> area.id == areaId }

        if (area != null) {
            val currentSettings = area.settings

            val areaSettings = setAreaSettings(settings, currentSettings)

            return AreaSettingsResponse(
                areaSettings.areaId!!,
                areaSettings.frequencyUnit,
                areaSettings.frequencyValue,
                areaSettings.isIrrigationEnabled,
                areaSettings.isWeatherEnabled,
                areaSettings.strength,
                areaSettings.threshhold
            )
        } else {
            throw SmartGardenException("Can't find area with given id: $areaId.", HttpStatus.NOT_FOUND)
        }
    }

    /** Update area settings or create defaults if there are nulls. */
    private fun setAreaSettings(settingsRequest: AreaSettingsRequest?, settings: AreaSettings): AreaSettings {
        if (settingsRequest != null) {
            if (settingsRequest.threshold != null)
                settings.threshhold = settingsRequest.threshold
            if (settingsRequest.frequencyUnit != null)
                settings.frequencyUnit = settingsRequest.frequencyUnit
            if (settingsRequest.frequencyValue != null)
                settings.frequencyValue = settingsRequest.frequencyValue
            if (settingsRequest.isIrrigationEnabled != null)
                settings.isIrrigationEnabled = settingsRequest.isIrrigationEnabled
            if (settingsRequest.isWeatherEnabled != null)
                settings.isWeatherEnabled = settingsRequest.isWeatherEnabled
            if (settingsRequest.strength != null)
                settings.strength = settingsRequest.strength
        }

        return settingsRepository.saveAndFlush(settings)
    }

    /** Retrieve settings of given area. */
    fun getAreaSettings(userId: Int, areaId: Int): AreaSettingsResponse {
        val userById = getUserById(userId)
        val device = userById.device!!
        val areaSettings = device.areas.first { area -> area.id == areaId }.settings

        return AreaSettingsResponse(
            areaSettings.areaId!!,
            areaSettings.frequencyUnit,
            areaSettings.frequencyValue,
            areaSettings.isIrrigationEnabled,
            areaSettings.isWeatherEnabled,
            areaSettings.strength,
            areaSettings.threshhold
        )
    }

    /** Irrigate given area. */
    fun irrigateArea(userId: Int, areaId: Int) {
        val user = getUserById(userId)
        val device = user.device!!
        val area = device.areas.firstOrNull { area -> area.id == areaId }

        if (area != null) {
            val settings = area.settings
            settings.irrigateNow = true

            settingsRepository.saveAndFlush(settings)
        } else {
            throw SmartGardenException("Can't find area with given id: $areaId.", HttpStatus.NOT_FOUND)
        }
    }

    /** Link sensor to area (and unlink from old one). */
    fun linkSensorToArea(userId: Int, areaId: Int, linkSensorRequest: LinkSensorRequest): SimpleAreaResponse {
        val user = getUserById(userId)
        val device = user.device!!
        val sensor = device.sensors.firstOrNull { sensor -> sensor.guid == linkSensorRequest.sensorGuid }

        if (sensor != null) {
            val area = device.areas.firstOrNull { area -> area.id == areaId }

            if (area != null) {
                val updatedArea = linkSensorToArea(area, sensor)

                return createSimpleAreaResponse(updatedArea, device.id)
            } else {
                throw SmartGardenException("Can't find area with given id: $areaId.", HttpStatus.NOT_FOUND)
            }
        } else {
            throw SmartGardenException("Sensor is not connected to your device.", HttpStatus.BAD_REQUEST)
        }
    }

    /** Link sensor to area if it's not already linked to it. */
    private fun linkSensorToArea(area: Area, sensor: Sensor): Area {
        if (area.sensors.firstOrNull { s -> sensor.guid == s.guid } == null) {
            area.sensors.add(sensor)

            return areaRepository.saveAndFlush(area)
        }

        return area
    }

    /** Unlink sensor form area - sensor will be not available in given area, but it's still connected to device. */
    fun unlinkSensorFromArea(userId: Int, areaId: Int, sensorGuid: String): SimpleAreaResponse {
        val user = getUserById(userId)
        val device = user.device!!
        val area = device.areas.firstOrNull { area -> area.id == areaId }

        if (area != null) {
            val sensor = area.sensors.firstOrNull { sensor -> sensor.guid == sensorGuid }

            if (sensor != null) {
                sensor.areaId = null
                sensorRepository.save(sensor)

                return createSimpleAreaResponse(area, device.id)
            } else {
                throw SmartGardenException("There is no sensor with given guid: $sensorGuid, connected to area: $areaId", HttpStatus.NOT_FOUND)
            }
        } else {
            throw SmartGardenException("There is no area with given id: $areaId", HttpStatus.NOT_FOUND)
        }
    }

    /** Retrieves all sensors. */
    fun getAllSensors(userId: Int, active: Boolean?): List<SensorResponse> {
        val device = getUserById(userId).device!!

        return device.sensors
            .filter { s -> active == null || s.isActive == active }
            .map { s -> SensorResponse(s.guid, s.type.name, s.type.unit, s.areaId, s.isActive) }
    }

    /** Retrieves all sensors. */
    fun getSensor(userId: Int, sensorGuid: String): SensorResponse {
        val device = getUserById(userId).device!!

        val sensor = device.sensors.firstOrNull { s -> s.guid == sensorGuid }
        if (sensor != null) {
            return SensorResponse(sensor.guid, sensor.type.name, sensor.type.unit, sensor.areaId, sensor.isActive)
        } else {
            throw SmartGardenException("Can't find sensor with given guid: $sensorGuid", HttpStatus.NOT_FOUND)
        }
    }

    /** Retrieves measures from all areas in given user device. */
    fun getAllAreasMeasures(userId: Int, dateFrom: Instant?, dateTo: Instant?): List<AreaMeasuresResponse> {
        val user = getUserById(userId)
        val device = user.device!!
        val areas = device.areas
        val result = mutableListOf<AreaMeasuresResponse>()

        for (area in areas) {
            result.add(getAreaMeasures(device, area.id, dateFrom, dateTo))
        }

        return result
    }

    /** Retrieve simple informations of all areas. */
    fun getAreasInfo(userId: Int): List<SimpleAreaResponse> {
        val result = mutableListOf<SimpleAreaResponse>()

        val areas = areaRepository.getUserAreas(userId)
        for (area in areas) {
            result.add(createSimpleAreaResponse(area, deviceRepository.getByUserId(userId).id))
        }

        return result
    }

    /** Retrieve simple informations of one area. */
    fun getAreaInfo(userId: Int, areaId: Int): SimpleAreaResponse {
        val device = getUserById(userId).device!!
        val area = device.areas.firstOrNull { area -> area.id == areaId }
        if (area != null) {
            return createSimpleAreaResponse(area, device.id)
        } else {
            throw SmartGardenException("Can't find area with given id: $areaId", HttpStatus.NOT_FOUND)
        }
    }

    /** Crate area. */
    fun createArea(userId: Int, request: CreateAreaRequest?): SimpleAreaResponse {
        val user = getUserById(userId)
        val device = user.device!!

        val areaSettings = AreaSettings()
        val area = Area(
            settings = areaSettings,
            deviceId = device.id,
            sensors = mutableListOf(),
            irrigations = mutableListOf(),
            measures = mutableListOf()
        )

        areaRepository.save(area)
        areaSettings.areaId = area.id

        setAreaSettings(request?.settings, areaSettings)

        areaRepository.saveAndFlush(area)

        request?.let {
            for (sensorGuid in request.sensors) {
                val sensor = device.sensors.firstOrNull { sensor -> sensor.guid == sensorGuid }
                if (sensor != null) {
                    linkSensorToArea(area, sensor)
                } else {
                    throw SmartGardenException("Can't find sensor with given guid: $sensorGuid", HttpStatus.NOT_FOUND)
                }
            }
        }

        return createSimpleAreaResponse(area, device.id)
    }

    private fun createSimpleAreaResponse(area: Area, deviceId: Int): SimpleAreaResponse {
        val irrigations = irrigationRepository.getLastIrrigation(area.id)
        var nextWateringTime: Instant? = null
        if (irrigations.isNotEmpty()) {
            nextWateringTime = irrigations[0].timestamp.plusSeconds(1L * area.settings.frequencyValue * area.settings.frequencyUnit.inSeconds)
        }

        return SimpleAreaResponse(
            area.id,
            AreaSettingsResponse(
                area.settings.areaId!!,
                area.settings.frequencyUnit,
                area.settings.frequencyValue,
                area.settings.isIrrigationEnabled,
                area.settings.isWeatherEnabled,
                area.settings.strength,
                area.settings.threshhold
            ),
            nextWateringTime,
            area.sensors.map { s -> SimpleAreaSensorResponse(s.guid, s.type.name, s.type.unit, s.isActive) },
            sensorRepository.findAllByAreaIdAndDeviceId(null, deviceId).map { s -> SimpleAreaSensorResponse(s.guid, s.type.name, s.type.unit, s.isActive) }
        )
    }

    fun deleteArea(userId: Int, areaId: Int) {
        val user = getUserById(userId)
        val device = user.device!!
        val area = device.areas.firstOrNull { area -> area.id == areaId }

        if (area != null) {
            device.areas.remove(area)
            settingsRepository.delete(area.settings)
            area.measures = mutableListOf()
            measureRepository.removeAllByAreaId(area.id)
            areaRepository.delete(area)
        } else {
            throw SmartGardenException("Can't find area with given id: $areaId.", HttpStatus.NOT_FOUND)
        }
    }

    private fun getUserById(id: Int): User {
        val userOptional = userRepository.findById(id)

        if (!userOptional.isPresent) throw SmartGardenException("Invalid token", HttpStatus.UNAUTHORIZED)

        return userOptional.get()
    }

    /** Retrieve all historical irrigations of given area. */
    fun getIrrigations(userId: Int, areaId: Int, from: Instant?, to: Instant?): List<AreaIrrigationResponse> {
        val user = getUserById(userId)
        val device = user.device!!
        val area = device.areas.firstOrNull { area -> area.id == areaId }

        if (area != null) {
            return area.irrigations
                .filter { irrigation ->
                    (from == null || irrigation.timestamp.isAfter(from))
                        && (to == null || irrigation.timestamp.isBefore(to))
                }
                .map { irrigation -> AreaIrrigationResponse(irrigation.timestamp, irrigation.amount) }
        } else {
            throw SmartGardenException("Can't find area with given id: $areaId.", HttpStatus.NOT_FOUND)
        }
    }

    /** Update sensor. */
    fun updateSensor(userId: Int, sensorGuid: String, request: SensorUpdateRequest): SensorResponse {
        val user = getUserById(userId)
        val device = user.device!!
        val sensor = device.sensors.firstOrNull { sensor -> sensor.guid == sensorGuid }

        if (sensor != null) {
            sensor.isActive = request.active
            val updatedSensor = sensorRepository.saveAndFlush(sensor)

            return SensorResponse(updatedSensor.guid, updatedSensor.type.name, updatedSensor.type.unit, updatedSensor.areaId, updatedSensor.isActive)
        } else {
            throw SmartGardenException("Can't find sensor with given guid: $sensorGuid", HttpStatus.NOT_FOUND)
        }
    }
}