package com.jobradar.app.data.mapper

import com.jobradar.app.data.remote.dto.CompanyDto
import com.jobradar.app.data.remote.dto.JobDto
import com.jobradar.app.domain.model.EducationLevel
import com.jobradar.app.domain.model.ExperienceLevel
import com.jobradar.app.domain.model.JobType
import org.junit.Assert.assertEquals
import org.junit.Test

class JobMapperTest {

    private val dto = JobDto(
        id = 42,
        title = "Android 工程师",
        city = "上海",
        salaryMinK = 25,
        salaryMaxK = 40,
        jobType = "全职",
        experience = "5-10年",
        education = "硕士",
        skills = listOf("Kotlin", "Compose"),
        description = "desc",
        company = CompanyDto(id = 1, name = "北辰科技", logoUrl = "logo", industry = "互联网"),
        dataSource = "demo",
        publishedAt = 1_700_000_000_000,
    )

    @Test
    fun `dto maps to domain with correct enums and fields`() {
        val domain = dto.toDomain()

        assertEquals(42L, domain.id)
        assertEquals("Android 工程师", domain.title)
        assertEquals("上海", domain.city)
        assertEquals(25, domain.salaryMinK)
        assertEquals(40, domain.salaryMaxK)
        assertEquals(JobType.FULL_TIME, domain.jobType)
        assertEquals(ExperienceLevel.SENIOR, domain.experience)
        assertEquals(EducationLevel.MASTER, domain.education)
        assertEquals(listOf("Kotlin", "Compose"), domain.skills)
        assertEquals("北辰科技", domain.company?.name)
    }

    @Test
    fun `unknown enum strings fall back to sensible defaults instead of crashing`() {
        val unknown = dto.copy(
            jobType = "未知类型",
            experience = "未知经验",
            education = "未知学历",
        )
        val domain = unknown.toDomain()
        // Falls back to the full-time / junior / bachelor defaults.
        assertEquals(JobType.FULL_TIME, domain.jobType)
        assertEquals(ExperienceLevel.JUNIOR, domain.experience)
        assertEquals(EducationLevel.BACHELOR, domain.education)
    }

    @Test
    fun `entity round-trips back to domain`() {
        val domain = dto.toDomain()
        val entity = domain.toEntity(matchScore = 88)
        val back = entity.toDomain()
        assertEquals(domain.id, back.id)
        assertEquals(domain.title, back.title)
        assertEquals(domain.salaryMinK, back.salaryMinK)
        assertEquals(domain.skills, back.skills)
    }
}
