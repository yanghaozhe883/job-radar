package com.jobradar.app.domain.usecase

import com.jobradar.app.domain.model.Job
import com.jobradar.app.domain.model.JobStatus
import com.jobradar.app.domain.repository.JobRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Fetch a single job's details for the detail screen. */
class GetJobDetailUseCase @Inject constructor(
    private val repository: JobRepository,
) {
    suspend operator fun invoke(jobId: Long): Job? = repository.getJobById(jobId)
}

/** Toggle favorite / apply / hide on a job. */
class SetJobStatusUseCase @Inject constructor(
    private val repository: JobRepository,
) {
    suspend operator fun invoke(jobId: Long, status: JobStatus) =
        repository.setJobStatus(jobId, status)
}

/** Observe a single job's status (favorite/applied...) for the detail screen. */
class ObserveJobStatusUseCase @Inject constructor(
    private val repository: JobRepository,
) {
    operator fun invoke(jobId: Long): Flow<JobStatus?> = repository.observeJobStatus(jobId)
}
