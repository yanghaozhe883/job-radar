package com.jobradar.provider

import com.jobradar.provider.JobProviderRegistry
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class JobProviderTest {

    private val registry = JobProviderRegistry(
        providers = listOf(MockJobProvider()),
        props = ProviderProperties(key = "mock"),
    )

    @Test
    fun `registry resolves the active provider by key`() {
        assertEquals("mock", registry.active.key)
    }

    @Test
    fun `mock provider search filters by city and keyword`() {
        val provider = registry.active
        val filtered = provider.search(JobSearchFilter(city = "上海", keyword = "智能体"))
        assertTrue(filtered.isNotEmpty())
        assertTrue(filtered.all { it.city == "上海" })
    }

    @Test
    fun `mock provider detail finds a job by id`() {
        val provider = registry.active
        val job = provider.detail("1")
        assertNotNull(job)
        assertEquals("iOS 开发工程师", job?.title)
        assertNull(provider.detail("999"))
    }

    @Test
    fun `mock provider company resolves by name`() {
        val provider = registry.active
        val company = provider.company("芥子科技")
        assertNotNull(company)
        assertEquals("芥子科技", company?.name)
    }

    @Test
    fun `mock provider search pages correctly`() {
        val provider = registry.active
        assertEquals(2, provider.search(JobSearchFilter(page = 1, pageSize = 2)).size)
        assertEquals(2, provider.search(JobSearchFilter(page = 2, pageSize = 2)).size)
    }
}
