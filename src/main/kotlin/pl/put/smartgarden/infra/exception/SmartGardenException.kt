package pl.put.smartgarden.infra.exception

import org.springframework.http.HttpStatus

/**
 * Default Smart Garden exception, other new exceptions should extend this one.
 */
open class SmartGardenException : RuntimeException {
    val status: HttpStatus

    constructor() : super() {
        this.status = HttpStatus.NOT_FOUND
    }

    constructor(status: HttpStatus) : super() {
        this.status = status
    }

    constructor(message: String?, status: HttpStatus) : super(message) {
        this.status = status
    }

    constructor(message: String?, cause: Throwable?, status: HttpStatus) : super(message, cause) {
        this.status = status
    }

    constructor(cause: Throwable?, status: HttpStatus) : super(cause) {
        this.status = status
    }

    constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean, status: HttpStatus) : super(message, cause, enableSuppression, writableStackTrace) {
        this.status = status
    }
}