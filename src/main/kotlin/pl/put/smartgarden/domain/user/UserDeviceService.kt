package pl.put.smartgarden.domain.user

import org.springframework.stereotype.Service
import pl.put.smartgarden.domain.device.Device
import pl.put.smartgarden.domain.device.dto.response.MeasureResponse
import pl.put.smartgarden.domain.device.dto.response.SensorResponse
import pl.put.smartgarden.domain.device.repository.DeviceRepository
import pl.put.smartgarden.domain.user.dto.request.IrrigationLevelRequest
import pl.put.smartgarden.domain.user.dto.request.LocationRequest
import pl.put.smartgarden.domain.user.dto.request.NextIrrigationRequest
import pl.put.smartgarden.domain.user.dto.response.AreaResponse
import pl.put.smartgarden.domain.user.dto.response.AreaSettingsResponse
import pl.put.smartgarden.domain.user.dto.response.UserGeneralSettingsResponse
import pl.put.smartgarden.domain.user.repository.UserRepository
import java.time.Instant

@Service
class UserDeviceService(
    val deviceRepository: DeviceRepository,
    val userRepository: UserRepository
) {

    fun createAndSaveDevice(deviceGuid: String, latitude: Double, longitude: Double, userId: Int) : Device {
        val device = Device(
            guid = deviceGuid,
            latitude = latitude,
            longitude = longitude,
            userId = userId
        )

        return deviceRepository.save(device)
    }

    fun saveDevice(device: Device) : Device = deviceRepository.save(device)

    fun setDeviceLocation(userId: Int, locationRequest: LocationRequest): UserGeneralSettingsResponse {
        val user = userRepository.getUserById(userId)

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

    fun getAreaMeasures(token: String, areaId: String, from: Instant, to: Instant): List<MeasureResponse> {
        TODO("Not yet implemented")
    }

    fun setIrrigationLevel(token: String, areaId: String, irrigationLevelRequest: IrrigationLevelRequest): AreaSettingsResponse {
        TODO("Not yet implemented")
    }

    fun getAreasSetting(token: String): List<AreaSettingsResponse> {
        TODO("Not yet implemented")
    }

    fun setNextIrrigationTime(token: String, areaId: String, irrigationTimeRequest: NextIrrigationRequest): NextIrrigationRequest {
        TODO("Not yet implemented")
    }

    fun irrigateArea(token: String, areaId: String) {
        TODO("Not yet implemented")
    }

    fun linkSensorToArea(token: String, areaId: String, sensorId: String): List<AreaResponse> {
        TODO("Not yet implemented")
    }

    fun unlinkSensorFromArea(token: String, sensorId: String): List<AreaResponse> {
        TODO("Not yet implemented")
    }

    fun getNotLinkedSensors(token: String): List<SensorResponse> {
        TODO("Not yet implemented")
    }

    fun getAvailableAreas() : List<AreaResponse> {
        TODO()
    }
}