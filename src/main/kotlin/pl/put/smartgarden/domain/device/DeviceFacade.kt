package pl.put.smartgarden.domain.device

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pl.put.smartgarden.domain.SecurityService
import pl.put.smartgarden.domain.ServiceRole
import pl.put.smartgarden.domain.device.SensorType.HUMIDITY
import pl.put.smartgarden.domain.device.SensorType.ILLUMINANCE
import pl.put.smartgarden.domain.device.SensorType.TEMPERATURE
import pl.put.smartgarden.domain.device.dto.request.DeviceRequest
import pl.put.smartgarden.domain.device.dto.request.MeasureRequest
import pl.put.smartgarden.domain.device.dto.response.AreaDecisionResponse
import pl.put.smartgarden.domain.device.dto.response.DeviceResponse
import pl.put.smartgarden.domain.device.dto.response.MeasureResponse
import pl.put.smartgarden.domain.device.dto.response.SensorResponse
import pl.put.smartgarden.domain.device.exception.NoSuchDeviceException
import java.time.Instant
import kotlin.math.max
import kotlin.math.roundToInt

@Service
class DeviceFacade(
    val deviceService: DeviceService,
    val sensorService: SensorService,
    val securityService: SecurityService,
    val measureService: MeasuseService,
    val areaService: AreaService,
    val irrigationService: IrrigationService,
    val weatherService: WeatherService
) {
    companion object {
        val logger = LoggerFactory.getLogger(DeviceFacade.javaClass)
    }

    fun createOrUpdateDevice(deviceRequest: DeviceRequest): DeviceResponse {
        val device = deviceService.getDeviceByGuid(deviceRequest.guid)
        device ?: throw NoSuchDeviceException()
        val sensors = sensorService.createSensors(device.id, deviceRequest.sensors)
            .filter { it.isActive }
            .map { SensorResponse(it.id, it.guid) }
        val token = securityService.generateJWTWithIdAndRole(device.id, ServiceRole.DEVICE)
        return DeviceResponse(token, sensors)
    }

    fun createMeasures(deviceMeasures: List<MeasureRequest>, token: String): List<MeasureResponse> {
        val measures = ArrayList<Measure>()
        val sensors = sensorService.getSensors()
        deviceMeasures.parallelStream().forEach {
            val measure = createMeasure(it, sensors)
            if (measure.areaId != null)
                measures.add(measure)
        }
        measureService.createMeasures(measures)
        return measures.map {
            MeasureResponse(
                it.id,
                it.timestamp,
                it.sensorId,
                it.value,
                sensorService.getUnitBySensorId(it.sensorId)
            )
        }
    }

    private fun createMeasure(deviceMeasure: MeasureRequest, sensors: List<Sensor>): Measure {
        val sensor = sensors.filter { it.id == deviceMeasure.sensorId}.firstOrNull { it.id == deviceMeasure.sensorId }
        if (sensor?.type == TEMPERATURE)
            return Measure(deviceMeasure.timestamp, deviceMeasure.value / 100, deviceMeasure.sensorId, sensor.areaId)
        else
            return Measure(deviceMeasure.timestamp, deviceMeasure.value, deviceMeasure.sensorId, sensor?.areaId)
    }

    fun getIrrigationDecisions(token: String): List<AreaDecisionResponse> {
        val deviceId = securityService.getIdFromToken(token)
        val areaDecisionResponse = ArrayList<AreaDecisionResponse>()
        val irrigationSensors = sensorService.getIrrigationSensorsByDeviceId(deviceId)
        val areas = areaService.getAllAreas()
        val isGoingToRain = weatherService.isGoingToRain(deviceService.getDeviceById(deviceId).get())
        irrigationSensors.filter { sensor -> sensor.areaId != null }
            .parallelStream()
            .forEach { sensor ->
                logger.error("Irrigation decision for sensor: {}", sensor.id)
                val area = areas.first {it.id == sensor.areaId!!}
                val irrigationValue = calculateIrrigation(area, isGoingToRain)
                if (irrigationValue > 0) {
                    irrigationService.createIrrigation(area, irrigationValue)
                    areaDecisionResponse.add(AreaDecisionResponse(sensor.guid, irrigationValue))
                    if (area.settings.irrigateNow) {
                        areaService.noLongerIrrigateNow(area)
                    }
                } else {
                    areaDecisionResponse.add(AreaDecisionResponse(sensor.guid, 0))
                }
            }
        irrigationSensors.filter { it.areaId == null }
            .forEach {
                areaDecisionResponse.add(AreaDecisionResponse(it.guid, 0))
            }

        return areaDecisionResponse
    }

    private fun calculateIrrigation(area: Area, isGoingToRain: Boolean): Int {
        if (irrigationIsDisabled(area)) {
            return 0
        }

        if (!timePassed(area)) {
            return 0
        }

        if (!isDryEnough(area)) {
            return 0
        }

        if (isIlluminanceTooHigh(area)) {
            return 0
        }

        if (isGoingToRain(area, isGoingToRain)) {
            return 0
        }

        return area.settings.strength + calculateExtraIrrigationAtTemperature(area)
    }

    private fun irrigationIsDisabled(area: Area) =
        area.settings.strength == 0 || (!area.settings.isIrrigationEnabled && !area.settings.irrigateNow)

    private fun timePassed(area: Area): Boolean {
        if (area.settings.irrigateNow) {
            return true
        }
        logger.error("Area irrigation size {}", area.irrigations.size)
        val irrigations = area.irrigations
        if (irrigations.size == 0) {
            return true
        }

        irrigations.sortedByDescending { it.timestamp }
        val lastIrrigation = irrigations.last()
        logger.error("Last irrigation at: {}", lastIrrigation)
        logger.error("First irrigation at: {}", irrigations.last().timestamp)
        logger.error("Now: {}", Instant.now())

        val startIrrigationAt = calculateIrrigationReadyTime(lastIrrigation, area)
        logger.error("Calculated start irrigation at: {}", startIrrigationAt)
        return Instant.now().isAfter(startIrrigationAt)
    }

    private fun calculateIrrigationReadyTime(lastIrrigation: Irrigation, area: Area) =
        lastIrrigation.timestamp.plusSeconds((area.settings.frequencyValue * area.settings.frequencyUnit.inSeconds).toLong())

    private fun isDryEnough(area: Area): Boolean {
        if (area.settings.irrigateNow) {
            return true
        }

        val averageMeasures = ArrayList<Int>()

        area.sensors
            .filter { it.type == HUMIDITY }
            .forEach { measure ->
            val measures = measure.measures.sortedByDescending { it.timestamp }
            if (measures.size < 2) {
                return false
            } else {
                averageMeasures.add((measures[0].value + measures[1].value)/2)
            }
            }
        return averageMeasures.average() < area.settings.threshhold
    }

    private fun isGoingToRain(area: Area, isGoingToRain: Boolean): Boolean {
        if (area.settings.irrigateNow) {
            return false
        }
        if (area.settings.isWeatherEnabled) {
            return isGoingToRain
        } else {
            return false
        }
    }

    private fun calculateExtraIrrigationAtTemperature(area: Area): Int {
        val averageMeasures = ArrayList<Int>()
        area.sensors
            .filter { it.type == TEMPERATURE }
            .forEach { measure ->
                val measures = measure.measures.sortedByDescending { it.timestamp }
                if (measures.size < 2) {
                    return 0
                } else {
                    averageMeasures.add((measures[0].value + measures[1].value) / 2)
                }
            }

        return ((max(averageMeasures.average(), 0.0) - 14) * 0.001 * area.settings.strength).roundToInt()
    }

    private fun isIlluminanceTooHigh(area: Area): Boolean {
        val averageMeasures = ArrayList<Int>()
        area.sensors
            .filter { it.type == ILLUMINANCE }
            .forEach { measure ->
                val measures = measure.measures.sortedByDescending { it.timestamp }
                if (measures.size < 2) {
                    return true
                } else {
                    averageMeasures.add((measures[0].value + measures[1].value)/2)
                }
            }
        return (averageMeasures.average()).roundToInt() > 10000
    }
}
