package pl.put.smartgarden.infra.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.put.smartgarden.domain.device.Device
import pl.put.smartgarden.domain.device.DeviceFacade
import pl.put.smartgarden.domain.device.dto.DeviceDto

@RestController
@RequestMapping("/devices")
class DeviceResource(
    val deviceFacade: DeviceFacade
) {
    @PostMapping
    fun createDevice(@RequestBody deviceDto: DeviceDto) {
        deviceFacade.createDevice(deviceDto)
    }

    @GetMapping
    fun getDevices(): List<Device> = deviceFacade.getDevices()
}