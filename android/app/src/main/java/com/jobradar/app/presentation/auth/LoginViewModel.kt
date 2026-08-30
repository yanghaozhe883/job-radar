package com.jobradar.app.presentation.auth

import androidx.lifecycle.viewModelScope
import com.jobradar.app.core.common.AppResult
import com.jobradar.app.core.mvi.MviViewModel
import com.jobradar.app.domain.model.AuthValidator
import com.jobradar.app.domain.usecase.SignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signIn: SignInUseCase,
) : MviViewModel<LoginContract.State, LoginContract.Event, LoginContract.Effect>(LoginContract.State()) {

    override suspend fun handleEvent(event: LoginContract.Event) {
        when (event) {
            is LoginContract.Event.OnPhoneChange -> reduce { it.copy(phone = event.phone, error = null) }
            is LoginContract.Event.OnCodeChange -> reduce { it.copy(code = event.code, error = null) }
            is LoginContract.Event.SendCode ->
                emitEffect(LoginContract.Effect.Toast(if (validatePhone()) "验证码已发送" else "请输入正确的手机号"))
            is LoginContract.Event.SignIn -> startSignIn()
        }
    }

    private fun startSignIn() {
        val s = state.value
        if (!validatePhone()) {
            emitEffect(LoginContract.Effect.Toast("请输入正确的手机号"))
            return
        }
        reduce { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            when (val result = signIn(s.phone, s.code)) {
                is AppResult.Success -> {
                    reduce { it.copy(isLoading = false) }
                    emitEffect(LoginContract.Effect.LoginSuccess(result.data))
                }
                is AppResult.Failure -> {
                    reduce { it.copy(isLoading = false, error = "登录失败，请检查手机号和验证码") }
                }
            }
        }
    }

    private fun validatePhone(): Boolean = AuthValidator.isValidPhone(state.value.phone)
}
