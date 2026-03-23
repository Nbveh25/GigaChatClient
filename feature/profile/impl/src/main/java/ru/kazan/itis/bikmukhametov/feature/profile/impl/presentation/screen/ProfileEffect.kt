package ru.kazan.itis.bikmukhametov.feature.profile.impl.presentation.screen

sealed interface ProfileEffect {
    data object OpenPhotoPicker : ProfileEffect
    data object SignedOut : ProfileEffect
    data class ShowError(val message: String) : ProfileEffect
}
