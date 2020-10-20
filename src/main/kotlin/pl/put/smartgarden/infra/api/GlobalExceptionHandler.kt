package pl.put.smartgarden.infra.api

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import pl.put.smartgarden.infra.exception.SmartGardenException
import java.util.*
import java.util.stream.Collectors


@ControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    override fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException,
                                              headers: HttpHeaders,
                                              status: HttpStatus, request: WebRequest): ResponseEntity<Any?> {
        val body: MutableMap<String, Any> = LinkedHashMap()
        body["timestamp"] = Date()
        val errors = ex.bindingResult
                .fieldErrors
                .stream()
                .map { x: FieldError -> x.defaultMessage }
                .collect(Collectors.toList())
        body["errors"] = errors
        return ResponseEntity(body, headers, status)
    }

    @ExceptionHandler(value = [(SmartGardenException::class)])
    fun handleUserAlreadyExistsException(ex: SmartGardenException, request: WebRequest): ResponseEntity<Any> {
        val body: MutableMap<String, Any> = LinkedHashMap()
        body["timestamp"] = Date()
        body["error"] = ex.message.toString()
        return handleExceptionInternal(ex, body, HttpHeaders.EMPTY, ex.status, request)
    }

    @ExceptionHandler(value = [(Exception::class)])
    fun handleAnyException(ex: Exception, request: WebRequest): ResponseEntity<Any> {
        val body: MutableMap<String, Any> = LinkedHashMap()
        body["timestamp"] = Date()
        body["error"] = ex.message.toString()
        return handleExceptionInternal(ex, body, HttpHeaders.EMPTY, HttpStatus.INTERNAL_SERVER_ERROR, request)
    }
}