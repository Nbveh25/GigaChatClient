package ru.kazan.itis.bikmukhametov.feature.auth.impl.presentation.screen

sealed interface AuthEffect {
    data class ShowSnackbar(val message: String) : AuthEffect
    data object NavigateToChats : AuthEffect
    data object NavigateToRegistration : AuthEffect
    data object StartGoogleSignInFlow : AuthEffect
}
