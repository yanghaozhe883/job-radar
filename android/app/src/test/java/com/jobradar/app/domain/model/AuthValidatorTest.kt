package com.jobradar.app.domain.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthValidatorTest {

    @Test
    fun `valid mainland phone passes`() {
        assertTrue(AuthValidator.isValidPhone("13800138000"))
    }

    @Test
    fun `invalid phone rejected`() {
        assertFalse(AuthValidator.isValidPhone("12345"))      // too short
        assertFalse(AuthValidator.isValidPhone("23800138000")) // must start with 1
        assertFalse(AuthValidator.isValidPhone("1380013800a")) // non-digit
        assertFalse(AuthValidator.isValidPhone(""))           // blank
    }

    @Test
    fun `six digit code passes`() {
        assertTrue(AuthValidator.isValidCode("123456"))
    }

    @Test
    fun `invalid code rejected`() {
        assertFalse(AuthValidator.isValidCode("12345"))   // too short
        assertFalse(AuthValidator.isValidCode("1234567")) // too long
        assertFalse(AuthValidator.isValidCode("12a456"))  // non-digit
        assertFalse(AuthValidator.isValidCode(""))        // blank
    }
}
