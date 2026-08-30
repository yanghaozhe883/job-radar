package com.jobradar.api

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException

/**
 * Translates exceptions into the unified [ApiResponse] envelope so the client
 * always receives a well-formed { code, message, data } object — never a raw
 * framework error body.
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(ApiException::class)
    fun handleApiException(e: ApiException): ResponseEntity<ApiResponse<Unit>> =
        ResponseEntity.status(statusFor(e.code)).body(
            ApiResponse.error(e.code, e.message ?: "请求失败")
        )

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(e: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Unit>> {
        val msg = e.bindingResult.fieldErrors.firstOrNull()?.defaultMessage ?: "参数校验失败"
        return ResponseEntity.badRequest().body(ApiResponse.error(ApiCode.BAD_REQUEST, msg))
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNotFound(e: NoResourceFoundException): ResponseEntity<ApiResponse<Unit>> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ApiResponse.error(ApiCode.NOT_FOUND, "资源不存在")
        )

    @ExceptionHandler(Exception::class)
    fun handleGeneric(e: Exception): ResponseEntity<ApiResponse<Unit>> {
        log.error("Unhandled exception", e)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ApiResponse.error(ApiCode.INTERNAL, "服务器内部错误")
        )
    }

    private fun statusFor(code: Int): HttpStatus = when (code) {
        ApiCode.NOT_FOUND -> HttpStatus.NOT_FOUND
        ApiCode.BAD_REQUEST -> HttpStatus.BAD_REQUEST
        else -> HttpStatus.INTERNAL_SERVER_ERROR
    }
}
