package com.jobradar.service

import com.jobradar.api.ApiException
import com.jobradar.api.dto.SortOption
import com.jobradar.domain.JobEntity
import com.jobradar.repo.JobRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.util.Optional

class JobServiceTest {

    private val jobs = listOf(
        JobEntity(id = 1, title = "A", city = "上海", salaryMinK = 20, salaryMaxK = 30, matchScore = 50, publishedAt = 100),
        JobEntity(id = 2, title = "B", city = "北京", salaryMinK = 30, salaryMaxK = 50, matchScore = 80, publishedAt = 300),
        JobEntity(id = 3, title = "C", city = "深圳", salaryMinK = 25, salaryMaxK = 40, matchScore = 60, publishedAt = 200),
    )

    private fun repo(): JobRepository {
        val m = mock(JobRepository::class.java)
        `when`(m.search(null, null, null, null)).thenReturn(jobs)
        `when`(m.findByOrderByPublishedAtDesc()).thenReturn(jobs)
        return m
    }

    private val service = JobService(repo())

    @Test
    fun `sort by match score is descending`() {
        val page = service.search(null, null, null, null, SortOption.MATCH, 1, 10)
        assertEquals(listOf("B", "C", "A"), page.items.map { it.title })
    }

    @Test
    fun `sort by salary is descending`() {
        val page = service.search(null, null, null, null, SortOption.SALARY, 1, 10)
        assertEquals(listOf("B", "C", "A"), page.items.map { it.title })
    }

    @Test
    fun `pagination sets hasMore and slices correctly`() {
        val page = service.search(null, null, null, null, SortOption.COMPREHENSIVE, 1, 2)
        assertEquals(2, page.items.size)
        assertEquals(3, page.total)
        assertTrue(page.hasMore)
        val page2 = service.search(null, null, null, null, SortOption.COMPREHENSIVE, 2, 2)
        assertEquals(1, page2.items.size)
        assertTrue(!page2.hasMore)
    }

    @Test
    fun `getById throws not found when missing`() {
        val m = mock(JobRepository::class.java)
        `when`(m.findById(999L)).thenReturn(Optional.empty())
        val svc = JobService(m)
        val e = assertThrows(ApiException::class.java) { svc.getById(999) }
        assertEquals(com.jobradar.api.ApiCode.NOT_FOUND, e.code)
    }
}
