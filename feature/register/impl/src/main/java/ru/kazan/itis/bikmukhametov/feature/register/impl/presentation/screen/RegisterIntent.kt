package ru.kazan.itis.bikmukhametov.feature.register.impl.presentation.screen

sealed interface RegisterIntent {
    data class EmailChanged(val value: String) : RegisterIntent
    data class PasswordChanged(val value: String) : RegisterIntent
    data class ConfirmPasswordChanged(val value: String) : RegisterIntent
    data object TogglePasswordVisibility : RegisterIntent
    data object ToggleConfirmPasswordVisibility : RegisterIntent
    data object RegisterButtonClicked : RegisterIntent
    data object RetryButtonClicked : RegisterIntent
}
