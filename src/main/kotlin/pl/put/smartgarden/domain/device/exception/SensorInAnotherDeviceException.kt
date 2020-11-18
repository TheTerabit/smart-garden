package pl.put.smartgarden.domain.device.exception

import org.springframework.http.HttpStatus
import pl.put.smartgarden.domain.SmartGardenException

class SensorInAnotherDeviceException : SmartGardenException {
    constructor() : super("Some sensors are already assigned to another device.", HttpStatus.CONFLICT)
}