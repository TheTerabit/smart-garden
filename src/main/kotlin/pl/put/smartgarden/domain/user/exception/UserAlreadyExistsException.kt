package pl.put.smartgarden.domain.user.exception

import org.springframework.http.HttpStatus
import pl.put.smartgarden.domain.SmartGardenException

class UserAlreadyExistsException : SmartGardenException {
    constructor() : super()
    constructor(status: HttpStatus) : super(status)
    constructor(message: String?, status: HttpStatus) : super(message, status)
    constructor(message: String?, cause: Throwable?, status: HttpStatus) : super(message, cause, status)
    constructor(cause: Throwable?, status: HttpStatus) : super(cause, status)
    constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean, status: HttpStatus) : super(message, cause, enableSuppression, writableStackTrace, status)
}