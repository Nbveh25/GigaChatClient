package ru.kazan.itis.bikmukhametov.feature.auth.impl.presentation.screen

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.kazan.itis.bikmukhametov.common.util.resource.StringResourceProvider
import ru.kazan.itis.bikmukhametov.common.util.viewmodel.BaseViewModel
import ru.kazan.itis.bikmukhametov.feature.auth.api.validation.InputValidator
import ru.kazan.itis.bikmukhametov.feature.auth.impl.R
import ru.kazan.itis.bikmukhametov.feature.auth.impl.data.NetworkException
import ru.kazan.itis.bikmukhametov.feature.auth.impl.domain.usecase.LoadAuthFormPreferencesUseCase
import ru.kazan.itis.bikmukhametov.feature.auth.impl.domain.usecase.SaveAuthSessionPreferencesUseCase
import ru.kazan.itis.bikmukhametov.feature.auth.impl.domain.usecase.SignInWithEmailPasswordUseCase

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInWithEmailPassword: SignInWithEmailPasswordUseCase,
    private val saveAuthSessionPreferences: SaveAuthSessionPreferencesUseCase,
    private val stringResourceProvider: StringResourceProvider,
    private val inputValidator: InputValidator,
    loadAuthFormPreferences: LoadAuthFormPreferencesUseCase,
) : BaseViewModel<AuthUiState, AuthIntent>(
    loadAuthFormPreferences().let { prefs ->
        AuthUiState(
            rememberMe = prefs.rememberMe,
            emailInput = prefs.savedEmail,
        )
    },
) {

    val uiState: StateFlow<AuthUiState> = state

    private val _effect = MutableSharedFlow<AuthEffect>(extraBufferCapacity = 64)
    val effect: SharedFlow<AuthEffect> = _effect.asSharedFlow()

    override fun onIntent(action: AuthIntent) {
        when (action) {
            is AuthIntent.EmailChanged -> updateState {
                copy(
                    emailInput = action.value,
                    isNetworkError = false,
                    passwordError = null,
                )
            }

            is AuthIntent.PasswordChanged -> updateState {
                copy(
                    passwordInput = action.value,
                    isNetworkError = false,
                    passwordError = null,
                )
            }

            AuthIntent.TogglePasswordVisibility -> updateState {
                copy(isPasswordVisible = !isPasswordVisible)
            }

            is AuthIntent.RememberMeChanged -> updateState {
                copy(rememberMe = action.checked)
            }

            AuthIntent.LoginButtonClicked -> performSignIn()

            AuthIntent.RetryButtonClicked -> {
                updateState { copy(isNetworkError = false) }
                performSignIn()
            }

            AuthIntent.GoogleSignInButtonClicked -> viewModelScope.launch {
                _effect.emit(AuthEffect.StartGoogleSignInFlow)
            }

            AuthIntent.RegistrationButtonClicked -> viewModelScope.launch {
                _effect.emit(AuthEffect.NavigateToRegistration)
            }
        }
    }

    private fun performSignIn() {
        val current = state.value
        val email = current.emailInput.trim()
        val password = current.passwordInput

        if (!inputValidator.isValidEmail(email)) {
            viewModelScope.launch {
                _effect.emit(
                    AuthEffect.ShowSnackbar(
                        stringResourceProvider.getString(R.string.error_invalid_email),
                    ),
                )
            }
            return
        }

        when (val passwordValidation = inputValidator.validatePassword(password)) {
            is InputValidator.ValidationResult.Failure -> {
                updateState {
                    copy(passwordError = passwordValidation.message)
                }
                return
            }
            InputValidator.ValidationResult.Success -> Unit
        }

        viewModelScope.launch {
            updateState {
                copy(
                    isLoading = true,
                    passwordError = null,
                    isNetworkError = false,
                )
            }
            signInWithEmailPassword(email, password)
                .onSuccess {
                    saveAuthSessionPreferences(email, current.rememberMe)
                    updateState { copy(isLoading = false) }
                    _effect.emit(AuthEffect.NavigateToChats)
                }
                .onFailure { e ->
                    updateState { copy(isLoading = false) }
                    when (e) {
                        is NetworkException -> {
                            updateState { copy(isNetworkError = true) }
                            _effect.emit(
                                AuthEffect.ShowSnackbar(
                                    stringResourceProvider.getString(R.string.error_network),
                                ),
                            )
                        }
                        else -> {
                            val msg = when (e.message) {
                                "short_password" -> stringResourceProvider.getString(
                                    R.string.auth_password_too_short,
                                )
                                else -> e.message?.takeIf { it.isNotBlank() }
                                    ?: stringResourceProvider.getString(R.string.error_unknown)
                            }
                            _effect.emit(AuthEffect.ShowSnackbar(msg))
                        }
                    }
                }
        }
    }
}
