package com.jobradar.app.domain.usecase

import com.jobradar.app.domain.model.Company
import com.jobradar.app.domain.model.EducationLevel
import com.jobradar.app.domain.model.ExperienceLevel
import com.jobradar.app.domain.model.Job
import com.jobradar.app.domain.model.JobType
import com.jobradar.app.domain.model.UserPreference
import org.junit.Assert.assertTrue
import org.junit.Test

class ScoreJobUseCaseTest {

    private val scoreJob = ScoreJobUseCase()

    private val perfectJob = Job(
        id = 1,
        title = "Android 工程师",
        city = "上海",
        salaryMinK = 25,
        salaryMaxK = 40,
        jobType = JobType.FULL_TIME,
        experience = ExperienceLevel.MID,
        education = EducationLevel.BACHELOR,
        skills = listOf("Kotlin", "Jetpack Compose"),
        company = Company(id = 1, name = "北辰科技", industry = "互联网"),
    )

    private val preference = UserPreference(
        city = "上海",
        targetRoles = listOf("Android 工程师"),
        skillTags = listOf("Kotlin"),
        salaryMinK = 20,
        salaryMaxK = 45,
    )

    @Test
    fun `highly matched job scores high`() {
        val score = scoreJob(perfectJob, preference)
        assertTrue("expected high score, got ${score.total}", score.total >= 60)
    }

    @Test
    fun `score stays within range zero to 100`() {
        val score = scoreJob(perfectJob, preference)
        assertTrue(score.total in 0..100)
    }

    @Test
    fun `reason is non-empty`() {
        val score = scoreJob(perfectJob, preference)
        assertTrue(score.reason.isNotBlank())
    }

    @Test
    fun `mismatched job scores lower`() {
        val mismatch = perfectJob.copy(
            title = "后勤专员",
            skills = listOf("招聘", "行政"),
            salaryMinK = 8,
            salaryMaxK = 12,
        )
        val score = scoreJob(mismatch, preference)
        assertTrue("expected lower than perfect", score.total < scoreJob(perfectJob, preference).total)
    }
}
