package pl.put.smartgarden.infra

import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import pl.put.smartgarden.domain.SmartGardenException
import java.util.Date
import java.util.LinkedHashMap
import javax.servlet.http.HttpServletRequest


@ControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any?> {
        val body: MutableMap<String, Any> = LinkedHashMap()
        body["timestamp"] = Date()
        val errors = ex.bindingResult
            .fieldErrors
            .map { x: FieldError -> x.defaultMessage }
        body["errors"] = errors
        return ResponseEntity(body, headers, status)
    }

    @ExceptionHandler(value = [(SmartGardenException::class)])
    fun handleSmartGardenException(ex: SmartGardenException, request: WebRequest): ResponseEntity<Any> {
        val body: MutableMap<String, Any> = LinkedHashMap()
        body["timestamp"] = Date()
        body["errors"] = listOf(ex.message.toString())
        return handleExceptionInternal(ex, body, HttpHeaders.EMPTY, ex.status, request)
    }

    @ExceptionHandler(value = [(Exception::class)])
    fun handleAnyException(ex: Exception, request: WebRequest): ResponseEntity<Any> {
        val body: MutableMap<String, Any> = LinkedHashMap()
        body["timestamp"] = Date()
        body["errors"] = listOf(ex.message.toString())
        return handleExceptionInternal(ex, body, HttpHeaders.EMPTY, HttpStatus.INTERNAL_SERVER_ERROR, request)
    }
}

@Controller
class GlobalErrorController : ErrorController {
    @RequestMapping("/error")
    @Throws(Throwable::class)
    fun handleError(request: HttpServletRequest) {
        if (request.getAttribute("javax.servlet.error.exception") != null) {
            throw (request.getAttribute("javax.servlet.error.exception") as Throwable)
        }
    }

    override fun getErrorPath(): String? = null
}