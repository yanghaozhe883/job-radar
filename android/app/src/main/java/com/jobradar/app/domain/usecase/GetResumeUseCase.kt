package com.jobradar.app.domain.usecase

import com.jobradar.app.domain.model.Resume
import com.jobradar.app.domain.repository.ResumeRepository
import javax.inject.Inject

/** Returns the user's resume. */
class GetResumeUseCase @Inject constructor(
    private val repository: ResumeRepository,
) {
    operator fun invoke(): Resume = repository.getResume()
}
