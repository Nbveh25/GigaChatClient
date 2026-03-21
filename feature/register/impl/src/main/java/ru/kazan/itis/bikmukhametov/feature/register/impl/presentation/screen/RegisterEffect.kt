package ru.kazan.itis.bikmukhametov.feature.register.impl.presentation.screen

sealed interface RegisterEffect {
    data class ShowSnackbar(val message: String) : RegisterEffect
    data object NavigateToLogin : RegisterEffect
}
