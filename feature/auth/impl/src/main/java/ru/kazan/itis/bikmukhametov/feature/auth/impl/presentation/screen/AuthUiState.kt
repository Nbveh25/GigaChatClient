package ru.kazan.itis.bikmukhametov.feature.auth.impl.presentation.screen

internal data class AuthUiState(
    val emailInput: String = "",
    val passwordInput: String = "",
    val isPasswordVisible: Boolean = false,
    val rememberMe: Boolean = false,
    val isLoading: Boolean = false,
    val passwordError: String? = null,
    val isNetworkError: Boolean = false,
) {
    val isButtonEnabled: Boolean
        get() = emailInput.isNotBlank() && passwordInput.isNotBlank()
}

