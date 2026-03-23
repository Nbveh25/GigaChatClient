package ru.kazan.itis.bikmukhametov.feature.auth.impl.presentation.screen

data class AuthUiState(
    // Ввод пользователя
    val emailInput: String = "",
    val passwordInput: String = "",
    val rememberMe: Boolean = false,
    val isPasswordVisible: Boolean = false,

    // Состояние валидации (обновляется через UseCase в ViewModel)
    val emailError: String? = null,
    val passwordError: String? = null,
    val isButtonEnabled: Boolean = false,

    // Состояние процесса
    val isLoading: Boolean = false,
    val isNetworkError: Boolean = false,
)

