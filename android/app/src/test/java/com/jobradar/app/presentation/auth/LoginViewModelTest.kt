package com.jobradar.app.presentation.auth

import app.cash.turbine.test
import com.jobradar.app.core.common.AppResult
import com.jobradar.app.core.mvi.MainDispatcherRule
import com.jobradar.app.domain.model.User
import com.jobradar.app.domain.usecase.SignInUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val signInUseCase: SignInUseCase = mockk()

    @Test
    fun `typing updates state and clears error`() = runTest {
        val vm = LoginViewModel(signInUseCase)
        vm.onEvent(LoginContract.Event.OnPhoneChange("138"))
        assertEquals("138", vm.state.value.phone)
        vm.onEvent(LoginContract.Event.OnCodeChange("123456"))
        assertEquals("123456", vm.state.value.code)
    }

    @Test
    fun `invalid phone on sign in shows toast instead of calling API`() = runTest {
        val vm = LoginViewModel(signInUseCase)
        vm.effect.test {
            vm.onEvent(LoginContract.Event.OnPhoneChange("123")) // invalid
            vm.onEvent(LoginContract.Event.SignIn)
            val effect = awaitItem()
            assertTrue(effect is LoginContract.Effect.Toast)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `valid sign in emits login success`() = runTest {
        val user = User(id = 1, phone = "13800138000", nickname = "求职者")
        coEvery { signInUseCase.invoke("13800138000", "123456") } returns AppResult.Success(user)

        val vm = LoginViewModel(signInUseCase)
        vm.effect.test {
            vm.onEvent(LoginContract.Event.OnPhoneChange("13800138000"))
            vm.onEvent(LoginContract.Event.OnCodeChange("123456"))
            vm.onEvent(LoginContract.Event.SignIn)
            val effect = awaitItem()
            assertTrue(effect is LoginContract.Effect.LoginSuccess)
            assertEquals(1L, (effect as LoginContract.Effect.LoginSuccess).user.id)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
