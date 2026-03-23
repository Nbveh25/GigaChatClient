package ru.kazan.itis.bikmukhametov.feature.profile.impl.presentation.screen

sealed interface ProfileIntent {
    data object LoadProfile : ProfileIntent
    data class UpdateUserName(val name: String) : ProfileIntent
    data object PhotoClicked : ProfileIntent
    data class PhotoSelected(val imageUriString: String) : ProfileIntent
    data class ThemeChanged(val isDarkTheme: Boolean) : ProfileIntent
    data object SignOutClicked : ProfileIntent
}
