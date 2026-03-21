package ru.kazan.itis.bikmukhametov.feature.register.impl.presentation.screen

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.kazan.itis.bikmukhametov.common.util.resource.StringResourceProvider
import ru.kazan.itis.bikmukhametov.common.util.viewmodel.BaseViewModel
import ru.kazan.itis.bikmukhametov.feature.auth.api.validation.InputValidator
import ru.kazan.itis.bikmukhametov.feature.register.api.usecase.RegisterUseCase
import ru.kazan.itis.bikmukhametov.feature.register.impl.R

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val stringResourceProvider: StringResourceProvider,
    private val inputValidator: InputValidator,
) : BaseViewModel<RegisterUiState, RegisterIntent>(RegisterUiState()) {

    private val _effect = MutableSharedFlow<RegisterEffect>(extraBufferCapacity = 64)
    val effect: SharedFlow<RegisterEffect> = _effect.asSharedFlow()

    override fun onIntent(action: RegisterIntent) {
        when (action) {
            is RegisterIntent.EmailChanged -> handleEmailChanged(action.value)
            is RegisterIntent.PasswordChanged -> handlePasswordChanged(action.value)
            is RegisterIntent.ConfirmPasswordChanged -> handleConfirmPasswordChanged(action.value)
            RegisterIntent.TogglePasswordVisibility -> updateState {
                copy(isPasswordVisible = !isPasswordVisible)
            }
            RegisterIntent.ToggleConfirmPasswordVisibility -> updateState {
                copy(isConfirmPasswordVisible = !isConfirmPasswordVisible)
            }
            RegisterIntent.RegisterButtonClicked -> performRegister()
            RegisterIntent.RetryButtonClicked -> {
                updateState { copy(isNetworkError = false) }
                performRegister()
            }
        }
    }

    private fun handleEmailChanged(email: String) {
        val emailError = if (email.isNotBlank() && !inputValidator.isValidEmail(email)) {
            stringResourceProvider.getString(R.string.register_error_invalid_email)
        } else {
            null
        }
        val s = state.value
        val isFormValid = validateForm(
            email = email,
            password = s.passwordInput,
            confirmPassword = s.confirmPasswordInput,
        )
        updateState {
            copy(
                emailInput = email,
                emailError = emailError,
                isNetworkError = false,
                isButtonEnabled = isFormValid && emailError == null &&
                    s.passwordError == null && s.confirmPasswordError == null,
            )
        }
    }

    private fun handlePasswordChanged(password: String) {
        val validationResult = inputValidator.validatePassword(password)
        val passwordError = (validationResult as? InputValidator.ValidationResult.Failure)?.message
        val s = state.value
        val confirmPasswordError = confirmMismatchError(password, s.confirmPasswordInput)
        val isFormValid = validateForm(
            email = s.emailInput,
            password = password,
            confirmPassword = s.confirmPasswordInput,
        )
        updateState {
            copy(
                passwordInput = password,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError,
                isNetworkError = false,
                isButtonEnabled = isFormValid && s.emailError == null &&
                    passwordError == null && confirmPasswordError == null,
            )
        }
    }

    private fun handleConfirmPasswordChanged(confirmPassword: String) {
        val s = state.value
        val confirmPasswordError = confirmMismatchError(s.passwordInput, confirmPassword)
        val isFormValid = validateForm(
            email = s.emailInput,
            password = s.passwordInput,
            confirmPassword = confirmPassword,
        )
        updateState {
            copy(
                confirmPasswordInput = confirmPassword,
                confirmPasswordError = confirmPasswordError,
                isNetworkError = false,
                isButtonEnabled = isFormValid && s.emailError == null &&
                    s.passwordError == null && confirmPasswordError == null,
            )
        }
    }

    private fun confirmMismatchError(password: String, confirmPassword: String): String? =
        if (confirmPassword.isNotBlank() && confirmPassword != password) {
            stringResourceProvider.getString(R.string.register_error_passwords_not_match)
        } else {
            null
        }

    private fun validateForm(email: String, password: String, confirmPassword: String): Boolean {
        val emailOk = inputValidator.isValidEmail(email)
        val passwordOk = inputValidator.validatePassword(password) is InputValidator.ValidationResult.Success
        val confirmOk = confirmPassword.isNotBlank() && confirmPassword == password
        return emailOk && passwordOk && confirmOk
    }

    private fun performRegister() {
        val current = state.value
        val email = current.emailInput.trim()
        val password = current.passwordInput

        if (!inputValidator.isValidEmail(email)) {
            viewModelScope.launch {
                _effect.emit(
                    RegisterEffect.ShowSnackbar(
                        stringResourceProvider.getString(R.string.register_error_invalid_email),
                    ),
                )
            }
            return
        }

        when (val passwordValidation = inputValidator.validatePassword(password)) {
            is InputValidator.ValidationResult.Failure -> {
                updateState { copy(passwordError = passwordValidation.message) }
                return
            }
            InputValidator.ValidationResult.Success -> Unit
        }

        if (current.confirmPasswordInput != password) {
            updateState {
                copy(
                    confirmPasswordError = stringResourceProvider.getString(
                        R.string.register_error_passwords_not_match,
                    ),
                )
            }
            viewModelScope.launch {
                _effect.emit(
                    RegisterEffect.ShowSnackbar(
                        stringResourceProvider.getString(R.string.register_message_validate_fields),
                    ),
                )
            }
            return
        }

        if (!current.isButtonEnabled) {
            viewModelScope.launch {
                _effect.emit(
                    RegisterEffect.ShowSnackbar(
                        stringResourceProvider.getString(R.string.register_message_validate_fields),
                    ),
                )
            }
            return
        }

        viewModelScope.launch {
            updateState {
                copy(
                    isLoading = true,
                    passwordError = null,
                    confirmPasswordError = null,
                    isNetworkError = false,
                )
            }
            registerUseCase(email, password)
                .onSuccess {
                    updateState { copy(isLoading = false) }
                    _effect.emit(RegisterEffect.NavigateToLogin)
                }
                .onFailure { e ->
                    updateState { copy(isLoading = false) }
                    when {
                        e is IOException -> {
                            updateState { copy(isNetworkError = true) }
                            _effect.emit(
                                RegisterEffect.ShowSnackbar(
                                    stringResourceProvider.getString(R.string.register_error_network),
                                ),
                            )
                        }
                        else -> {
                            val msg = e.message?.takeIf { it.isNotBlank() }
                                ?: stringResourceProvider.getString(R.string.register_error_register)
                            _effect.emit(RegisterEffect.ShowSnackbar(msg))
                        }
                    }
                }
        }
    }
}
