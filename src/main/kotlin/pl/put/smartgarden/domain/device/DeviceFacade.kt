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
import pl.put.smartgarden.domain.device.dto.response.SensorResponse

@Service
class DeviceFacade(
    val deviceService: DeviceService,
    val sensorService: SensorService,
    val securityService: SecurityService,
    val measureService: MeasuseService
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
        //getSensors
        //getSettings
        //getWeather
        //calculate

        return sensorService.getIrrigationSensorsByDeviceId(securityService.getIdFromToken(token)).map {sensor -> AreaDecisionResponse(sensor.guid, true)}
    }
}