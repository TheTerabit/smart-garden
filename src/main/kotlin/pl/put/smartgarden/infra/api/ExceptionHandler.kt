package pl.put.smartgarden.infra.api

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.util.*
import java.util.stream.Collectors


@ControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {

    override fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException,
                                              headers: HttpHeaders,
                                              status: HttpStatus, request: WebRequest): ResponseEntity<Any?> {
        val body: MutableMap<String, Any> = LinkedHashMap()
        body["timestamp"] = Date()
        body["status"] = status.value()

        val errors = ex.bindingResult
                .fieldErrors
                .stream()
                .map { x: FieldError -> x.defaultMessage }
                .collect(Collectors.toList())
        body["errors"] = errors
        return ResponseEntity(body, headers, status)
    }
}