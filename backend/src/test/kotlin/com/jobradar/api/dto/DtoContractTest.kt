package com.jobradar.api.dto

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Verifies the shared API contract actually round-trips: the `{ code, message,
 * data }` envelope and snake_case field names must deserialize into the DTOs the
 * backend serves (and the Android client expects).
 */
class DtoContractTest {

    private val mapper = ObjectMapper().registerKotlinModule()

    @Test
    fun `job dto parses snake_case envelope`() {
        val json = """
            {"code":0,"message":"ok","data":{
              "id":42,"title":"Android 工程师","city":"上海",
              "salary_min_k":25,"salary_max_k":40,"job_type":"全职",
              "experience":"3-5年","education":"本科",
              "skills":["Kotlin","Compose"],"data_source":"demo",
              "company":{"id":1,"name":"北辰科技","industry":"互联网"}
            }}
        """.trimIndent()

        val resp = mapper.readValue(json, ApiResponseDto::class.java)
        assertTrue(resp.code == 0)
        assertEquals("Android 工程师", resp.data?.title)
        assertEquals(25, resp.data?.salaryMinK)
        assertEquals(40, resp.data?.salaryMaxK)
        assertEquals(listOf("Kotlin", "Compose"), resp.data?.skills)
        assertEquals("北辰科技", resp.data?.company?.name)
    }

    @Test
    fun `job dto serializes snake_case for the client`() {
        val dto = JobDto(
            id = 7, title = "后端", city = "北京",
            salaryMinK = 20, salaryMaxK = 35,
        )
        val json = mapper.writeValueAsString(dto)
        assertTrue(json.contains("salary_min_k"), "should be snake_case")
        assertTrue(json.contains("salary_max_k"))
        assertTrue(json.contains("\"job_type\""))
        assertTrue(json.contains("\"data_source\""))
    }
}

/** A thin typed holder so we can decode the envelope for the test. */
private data class ApiResponseDto(
    val code: Int = 0,
    val message: String = "",
    val data: JobDto? = null,
)
