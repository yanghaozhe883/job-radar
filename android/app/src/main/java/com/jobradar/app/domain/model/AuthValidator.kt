package com.jobradar.app.domain.model

/**
 * Pure, framework-free input validation for the login flow. Kept in the domain
 * layer so it's trivially unit-testable without Android.
 */
object AuthValidator {

    /** A mainland China mobile number: 11 digits starting with 1. */
    fun isValidPhone(phone: String): Boolean =
        Regex("^1\\d{10}$").matches(phone.trim())

    /** A 6-digit numeric verification code. */
    fun isValidCode(code: String): Boolean =
        code.length == 6 && code.all { it.isDigit() }
}
