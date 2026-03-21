package ru.kazan.itis.bikmukhametov.feature.auth.impl.presentation.screen

internal sealed interface AuthIntent {
    data class EmailChanged(val value: String) : AuthIntent
    data class PasswordChanged(val value: String) : AuthIntent
    data object TogglePasswordVisibility : AuthIntent
    data class RememberMeChanged(val checked: Boolean) : AuthIntent
    data object LoginButtonClicked : AuthIntent
    data object RetryButtonClicked : AuthIntent
    data object GoogleSignInButtonClicked : AuthIntent
    data object RegistrationButtonClicked : AuthIntent
}
