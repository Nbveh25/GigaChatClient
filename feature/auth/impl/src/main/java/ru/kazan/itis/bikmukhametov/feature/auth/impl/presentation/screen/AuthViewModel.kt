package ru.kazan.itis.bikmukhametov.feature.auth.impl.presentation.screen

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
import ru.kazan.itis.bikmukhametov.feature.auth.api.usecase.GetCurrentUserUseCase
import ru.kazan.itis.bikmukhametov.feature.auth.api.usecase.SignInWithEmailPasswordUseCase
import ru.kazan.itis.bikmukhametov.feature.auth.api.usecase.ValidateLoginUseCase
import ru.kazan.itis.bikmukhametov.feature.auth.api.validation.InputValidator
import ru.kazan.itis.bikmukhametov.feature.auth.impl.R

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInWithEmailPassword: SignInWithEmailPasswordUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val validateLoginUseCase: ValidateLoginUseCase, // Новый UseCase
    private val stringResourceProvider: StringResourceProvider,
) : BaseViewModel<AuthUiState, AuthIntent>(AuthUiState()) {

    private val _effect = MutableSharedFlow<AuthEffect>(extraBufferCapacity = 64)
    val effect: SharedFlow<AuthEffect> = _effect.asSharedFlow()

    init {
        checkAutoLogin()
        observeValidation()
    }

    private fun observeValidation() {
        state
            .map { s -> validateLoginUseCase(s.emailInput, s.passwordInput) }
            .distinctUntilChanged()
            .onEach { result ->
                updateState {
                    copy(
                        emailError = result.emailError,
                        passwordError = result.passwordError,
                        isButtonEnabled = result.isValid
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onIntent(action: AuthIntent) {
        when (action) {
            is AuthIntent.EmailChanged -> updateState {
                copy(emailInput = action.value, isNetworkError = false)
            }

            is AuthIntent.PasswordChanged -> updateState {
                copy(passwordInput = action.value, isNetworkError = false)
            }

            is AuthIntent.TogglePasswordVisibility -> updateState {
                copy(isPasswordVisible = !isPasswordVisible)
            }

            is AuthIntent.RememberMeChanged -> updateState {
                copy(rememberMe = action.checked)
            }

            is AuthIntent.LoginButtonClicked -> performSignIn()
            is AuthIntent.RetryButtonClicked -> performSignIn()
            is AuthIntent.GoogleSignInButtonClicked -> emitEffect(AuthEffect.StartGoogleSignInFlow)
            is AuthIntent.RegistrationButtonClicked -> emitEffect(AuthEffect.NavigateToRegistration)
        }
    }

    private fun performSignIn() {
        val current = state.value
        if (!current.isButtonEnabled) return

        viewModelScope.launch {
            updateState { copy(isLoading = true, isNetworkError = false) }

            signInWithEmailPassword(current.emailInput.trim(), current.passwordInput)
                .onSuccess {
                    updateState { copy(isLoading = false) }
                    _effect.emit(AuthEffect.NavigateToChats)
                }
                .onFailure { e ->
                    updateState { copy(isLoading = false) }
                    handleError(e)
                }
        }
    }

    private fun emitEffect(effect: AuthEffect) {
        viewModelScope.launch { _effect.emit(effect) }
    }

    private suspend fun handleError(e: Throwable) {
        val message = when (e) {
            is IOException -> {
                updateState { copy(isNetworkError = true) }
                stringResourceProvider.getString(R.string.error_network)
            }

            else -> e.message?.takeIf { it.isNotBlank() }
                ?: stringResourceProvider.getString(R.string.error_unknown)
        }
        _effect.emit(AuthEffect.ShowSnackbar(message))
    }

    private fun checkAutoLogin() {
        viewModelScope.launch {
            if (getCurrentUserUseCase() != null) {
                _effect.emit(AuthEffect.NavigateToChats)
            }
        }
    }
}