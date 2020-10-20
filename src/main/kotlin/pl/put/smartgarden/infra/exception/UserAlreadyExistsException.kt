package pl.put.smartgarden.infra.exception

import org.springframework.http.HttpStatus

class UserAlreadyExistsException : SmartGardenException {
    constructor() : super()
    constructor(status: HttpStatus) : super(status)
    constructor(message: String?, status: HttpStatus) : super(message, status)
    constructor(message: String?, cause: Throwable?, status: HttpStatus) : super(message, cause, status)
    constructor(cause: Throwable?, status: HttpStatus) : super(cause, status)
    constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean, status: HttpStatus) : super(message, cause, enableSuppression, writableStackTrace, status)
}