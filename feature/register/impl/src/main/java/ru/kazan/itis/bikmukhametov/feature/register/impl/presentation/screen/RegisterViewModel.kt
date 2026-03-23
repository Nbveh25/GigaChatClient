package ru.kazan.itis.bikmukhametov.feature.register.impl.presentation.screen

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.kazan.itis.bikmukhametov.common.util.resource.StringResourceProvider
import ru.kazan.itis.bikmukhametov.common.util.viewmodel.BaseViewModel
import ru.kazan.itis.bikmukhametov.feature.auth.api.validation.InputValidator
import ru.kazan.itis.bikmukhametov.feature.register.api.usecase.RegisterUseCase
import ru.kazan.itis.bikmukhametov.feature.register.api.usecase.ValidateRegistrationUseCase
import ru.kazan.itis.bikmukhametov.feature.register.impl.R

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val validateRegistrationUseCase: ValidateRegistrationUseCase,
    private val stringResourceProvider: StringResourceProvider,
) : BaseViewModel<RegisterUiState, RegisterIntent>(RegisterUiState()) {

    private val _effect = MutableSharedFlow<RegisterEffect>(extraBufferCapacity = 64)
    val effect: SharedFlow<RegisterEffect> = _effect.asSharedFlow()

    init {
        observeValidation()
    }

    override fun onIntent(action: RegisterIntent) {
        when (action) {
            is RegisterIntent.EmailChanged -> updateState {
                copy(
                    emailInput = action.value,
                    isNetworkError = false
                )
            }

            is RegisterIntent.PasswordChanged -> updateState {
                copy(
                    passwordInput = action.value,
                    isNetworkError = false
                )
            }

            is RegisterIntent.ConfirmPasswordChanged -> updateState {
                copy(
                    confirmPasswordInput = action.value,
                    isNetworkError = false
                )
            }

            is RegisterIntent.TogglePasswordVisibility -> updateState {
                copy(isPasswordVisible = !isPasswordVisible)
            }

            is RegisterIntent.ToggleConfirmPasswordVisibility -> updateState {
                copy(
                    isConfirmPasswordVisible = !isConfirmPasswordVisible
                )
            }

            is RegisterIntent.RegisterButtonClicked -> performRegister()
            is RegisterIntent.RetryButtonClicked -> performRegister()
        }
    }

    private fun observeValidation() {
        // Автоматически обновляем состояние кнопки при любом изменении полей
        state
            .map { s ->
                validateRegistrationUseCase(s.emailInput, s.passwordInput, s.confirmPasswordInput)
            }
            .distinctUntilChanged()
            .onEach { result ->
                updateState {
                    copy(
                        emailError = result.emailError,
                        passwordError = result.passwordError,
                        confirmPasswordError = result.confirmPasswordError,
                        isButtonEnabled = result.isValid
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun performRegister() {
        val current = state.value
        if (!current.isButtonEnabled) return

        viewModelScope.launch {
            updateState { copy(isLoading = true, isNetworkError = false) }

            registerUseCase(current.emailInput.trim(), current.passwordInput)
                .onSuccess {
                    updateState { copy(isLoading = false) }
                    _effect.emit(RegisterEffect.NavigateToLogin)
                }
                .onFailure { e ->
                    updateState { copy(isLoading = false) }
                    handleRegistrationError(e)
                }
        }
    }

    private suspend fun handleRegistrationError(e: Throwable) {
        val message = when (e) {
            is IOException -> {
                updateState { copy(isNetworkError = true) }
                stringResourceProvider.getString(R.string.register_error_network)
            }

            else -> e.message?.takeIf { it.isNotBlank() }
                ?: stringResourceProvider.getString(R.string.register_error_register)
        }
        _effect.emit(RegisterEffect.ShowSnackbar(message))
    }
}
