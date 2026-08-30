package com.jobradar.app.presentation.jobs

import app.cash.turbine.test
import com.jobradar.app.core.common.AppResult
import com.jobradar.app.core.common.AppError
import com.jobradar.app.core.mvi.MainDispatcherRule
import com.jobradar.app.domain.model.JobFilter
import com.jobradar.app.domain.model.Sort
import com.jobradar.app.domain.usecase.JobUi
import com.jobradar.app.domain.usecase.ObserveJobsUseCase
import com.jobradar.app.domain.usecase.RefreshJobsUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class JobsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val observeJobs: ObserveJobsUseCase = mockk()
    private val refreshJobs: RefreshJobsUseCase = mockk()

    @Test
    fun `initial refresh success loads jobs`() = runTest {
        every { observeJobs.invoke(any()) } returns flowOf(emptyList())
        coEvery { refreshJobs.invoke(any()) } returns AppResult.Success(emptyList())

        val vm = JobsViewModel(observeJobs, refreshJobs)

        assertEquals(false, vm.state.value.isLoading)
        assertEquals(null, vm.state.value.error)
    }

    @Test
    fun `refresh failure sets error`() = runTest {
        every { observeJobs.invoke(any()) } returns flowOf(emptyList())
        coEvery { refreshJobs.invoke(any()) } returns AppResult.Failure(AppError.Network)

        val vm = JobsViewModel(observeJobs, refreshJobs)

        assertTrue(vm.state.value.error?.isNotBlank() == true)
    }

    @Test
    fun `job click emits navigate effect`() = runTest {
        every { observeJobs.invoke(any()) } returns flowOf(emptyList())
        coEvery { refreshJobs.invoke(any()) } returns AppResult.Success(emptyList())

        val vm = JobsViewModel(observeJobs, refreshJobs)
        vm.effect.test {
            vm.onEvent(JobsContract.Event.OnJobClick(7L))
            val effect = awaitItem()
            assertTrue(effect is JobsContract.Effect.NavigateToJob)
            assertEquals(7L, (effect as JobsContract.Effect.NavigateToJob).jobId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `sort change re-subscribes with the new filter`() = runTest {
        val emitted = MutableSharedFlow<List<JobUi>>(extraBufferCapacity = 1)
        every { observeJobs.invoke(any()) } returns emitted
        coEvery { refreshJobs.invoke(any()) } returns AppResult.Success(emptyList())

        val vm = JobsViewModel(observeJobs, refreshJobs)
        vm.onEvent(JobsContract.Event.OnSortChange(JobFilter(sort = Sort.SALARY)))

        // The use case should be re-subscribed with the SALARY sort.
        io.mockk.verify { observeJobs.invoke(JobFilter(sort = Sort.SALARY)) }
    }
}
