package com.jobradar.app.presentation.radar

import app.cash.turbine.test
import com.jobradar.app.core.mvi.MainDispatcherRule
import com.jobradar.app.domain.model.Company
import com.jobradar.app.domain.model.EducationLevel
import com.jobradar.app.domain.model.ExperienceLevel
import com.jobradar.app.domain.model.Job
import com.jobradar.app.domain.model.JobPushSignal
import com.jobradar.app.domain.model.JobType
import com.jobradar.app.domain.model.MatchScore
import com.jobradar.app.domain.usecase.JobUi
import com.jobradar.app.domain.usecase.ObserveJobPushSignalsUseCase
import com.jobradar.app.domain.usecase.ObserveRadarHitsUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RadarViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val hitsUseCase: ObserveRadarHitsUseCase = mockk()
    private val pushUseCase: ObserveJobPushSignalsUseCase = mockk()

    private val job = Job(
        id = 1, title = "Android 工程师", city = "上海",
        salaryMinK = 25, salaryMaxK = 40,
        jobType = JobType.FULL_TIME, experience = ExperienceLevel.MID, education = EducationLevel.BACHELOR,
        skills = listOf("Kotlin", "Compose"), company = Company(id = 1, name = "北辰科技"),
    )

    @Test
    fun `radar hits populate state and set last detected`() = runTest {
        every { hitsUseCase.invoke(5) } returns flowOf(
            listOf(JobUi(job, MatchScore(90, 40, 35, 15, "reason")))
        )
        every { pushUseCase.invoke() } returns MutableSharedFlow()

        val vm = RadarViewModel(hitsUseCase, pushUseCase)

        assertEquals(1, vm.state.value.radarHits.size)
        assertEquals(1L, vm.state.value.lastDetectedJobId)
    }

    @Test
    fun `live signal sets detection state and emits effects`() = runTest {
        every { hitsUseCase.invoke(5) } returns flowOf(emptyList())
        val signals = MutableSharedFlow<JobPushSignal>()
        every { pushUseCase.invoke() } returns signals

        val vm = RadarViewModel(hitsUseCase, pushUseCase)
        vm.effect.test {
            signals.emit(JobPushSignal(jobId = 99, title = "新岗位", matchScore = 70))
            // state updated
            assertEquals(99L, vm.state.value.lastDetectedJobId)
            assertEquals(99L, vm.state.value.liveSignal?.jobId)
            // both effects emitted
            assertTrue(awaitItem() is RadarContract.Effect.VibrateOnDetection)
            assertTrue(awaitItem() is RadarContract.Effect.Toast)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `job click emits navigate effect`() = runTest {
        every { hitsUseCase.invoke(5) } returns flowOf(emptyList())
        every { pushUseCase.invoke() } returns MutableSharedFlow()

        val vm = RadarViewModel(hitsUseCase, pushUseCase)
        vm.effect.test {
            vm.onEvent(RadarContract.Event.OnJobClick(42))
            val effect = awaitItem()
            assertTrue(effect is RadarContract.Effect.NavigateToJob)
            assertEquals(42L, (effect as RadarContract.Effect.NavigateToJob).jobId)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
