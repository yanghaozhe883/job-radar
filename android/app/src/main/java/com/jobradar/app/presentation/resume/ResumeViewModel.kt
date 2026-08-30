package com.jobradar.app.presentation.resume

import com.jobradar.app.core.mvi.MviViewModel
import com.jobradar.app.domain.usecase.GetResumeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ResumeViewModel @Inject constructor(
    private val getResume: GetResumeUseCase,
) : MviViewModel<ResumeContract.State, ResumeContract.Event, ResumeContract.Effect>(
    ResumeContract.State()
) {

    override suspend fun handleEvent(event: ResumeContract.Event) {
        when (event) {
            is ResumeContract.Event.OnEnter -> {
                val resume = getResume()
                reduce { it.copy(resume = resume, isLoading = false) }
            }
        }
    }
}
