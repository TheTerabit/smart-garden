package pl.put.smartgarden.domain.device

import org.springframework.stereotype.Service
import pl.put.smartgarden.domain.device.dto.request.DeviceRequest
import pl.put.smartgarden.domain.device.dto.request.MeasureRequest
import pl.put.smartgarden.domain.device.dto.response.AreaDecisionResponse
import pl.put.smartgarden.domain.device.dto.response.DeviceResponse
import pl.put.smartgarden.domain.device.dto.response.MeasureResponse
import pl.put.smartgarden.domain.device.exception.NoSuchDeviceException
import pl.put.smartgarden.domain.SecurityService
import pl.put.smartgarden.domain.ServiceRole
import pl.put.smartgarden.domain.device.SensorType.HUMIDITY
import pl.put.smartgarden.domain.device.SensorType.ILLUMINANCE
import pl.put.smartgarden.domain.device.SensorType.TEMPERATURE
import pl.put.smartgarden.domain.device.dto.response.SensorResponse
import java.time.Instant
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
        return measureService.createMeasures(deviceMeasures)
            .map { measure ->
                MeasureResponse(
                    measure.id,
                    measure.timestamp,
                    measure.sensorId,
                    measure.value,
                    sensorService.getUnitBySensorId(measure.sensorId)
                )
            }
    }

    fun getIrrigationDecisions(token: String): List<AreaDecisionResponse> {
        val deviceId = securityService.getIdFromToken(token)
        val areaDecisionResponse = ArrayList<AreaDecisionResponse>()
        val irrigationSensors = sensorService.getIrrigationSensorsByDeviceId(deviceId)

        irrigationSensors.filter { sensor -> sensor.areaId != null }
            .forEach { sensor ->
                val area = areaService.getAreaById(sensor.areaId!!)!!
                val irrigationValue = calculateIrrigation(area)
                if (irrigationValue > 0) {
                    irrigationService.createIrrigation(area, irrigationValue)
                    areaDecisionResponse.add(AreaDecisionResponse(sensor.guid, irrigationValue))
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

    private fun calculateIrrigation(area: Area): Int {
        if (irrigationIsDisabled(area)) {
            return 0
        }

        if (!timePassed(area)) {
            return 0
        }

        if (!isDryEnough(area)) {
            return 0
        }

        if (isGoingToRain(area)) {
            return 0
        }

        return area.settings.strength + calculateExtraIrrigationAtTemperature(area) + calculateExtraIrrigationAtIlluminance(area)
    }

    private fun irrigationIsDisabled(area: Area) =
        area.settings.strength == 0 || (!area.settings.isIrrigationEnabled && !area.settings.irrigateNow)

    private fun timePassed(area: Area): Boolean {
        if (area.settings.irrigateNow) {
            return true
        }

        val irrigations = area.irrigations
        if (irrigations.size == 0) {
            return true
        }

        irrigations.sortedByDescending { it.timestamp }
        val lastIrrigation = irrigations[0]
        val startIrrigationAt = calculateIrrigationReadyTime(lastIrrigation, area)

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

    private fun isGoingToRain(area: Area): Boolean {
        if (area.settings.irrigateNow) {
            return true
        }
        if (area.settings.isWeatherEnabled) {
            return weatherService.isGoingToRain(area.deviceId)
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
                    averageMeasures.add((measures[0].value + measures[1].value)/2)
                }
            }
        return (averageMeasures.average() - 20 * 0.1).roundToInt()// TODO - zmienić odpowiednio to równanie
    }

    private fun calculateExtraIrrigationAtIlluminance(area: Area): Int {
        val averageMeasures = ArrayList<Int>()
        area.sensors
            .filter { it.type == ILLUMINANCE }
            .forEach { measure ->
                val measures = measure.measures.sortedByDescending { it.timestamp }
                if (measures.size < 2) {
                    return 0
                } else {
                    averageMeasures.add((measures[0].value + measures[1].value)/2)
                }
            }
        return (averageMeasures.average() * 0.01).roundToInt()// TODO - zmienić odpowiednio to równanie
    }
}
