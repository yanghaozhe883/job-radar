package com.jobradar

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

/**
 * Integration test: boots the whole Spring context and hits real endpoints to
 * verify the shared `{ code, message, data }` envelope is actually what the
 * client receives (not just a unit test of a DTO). This is the "contract" test
 * that proves front/back agreement.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = ["demo.seed=true", "crawler.enabled=false"])
class ApiContractIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `GET jobs returns the standard envelope`() {
        val body = mockMvc.perform(get("/api/v1/jobs")).andReturn().response.contentAsString
        assertTrue(body.contains("\"code\":0"), "should carry code=0, got: $body")
        assertTrue(body.contains("\"message\""), "should carry message, got: $body")
        assertTrue(body.contains("\"data\""), "should carry data, got: $body")
        assertTrue(body.contains("salary_min_k"), "should be snake_case, got: $body")
    }

    @Test
    fun `GET job by id returns data`() {
        // With demo.seed=true there is at least one seeded job; fetch by id=1.
        val body = mockMvc.perform(get("/api/v1/jobs/1")).andReturn().response.contentAsString
        assertTrue(body.contains("\"code\":0"), "got: $body")
    }

    @Test
    fun `unknown job returns code 404`() {
        val body = mockMvc.perform(get("/api/v1/jobs/999999")).andReturn().response.contentAsString
        assertTrue(body.contains("\"code\":404"), "expected 404 envelope, got: $body")
        assertTrue(body.contains("\"message\""), "expected a message field, got: $body")
    }
}
