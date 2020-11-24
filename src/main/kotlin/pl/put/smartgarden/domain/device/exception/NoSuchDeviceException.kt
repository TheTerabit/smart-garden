package pl.put.smartgarden.domain.device.exception

import org.springframework.http.HttpStatus
import pl.put.smartgarden.domain.SmartGardenException

class NoSuchDeviceException : SmartGardenException {
    constructor() : super("Device is not assigned to any user.", HttpStatus.NOT_FOUND)
}
