package ru.kazan.itis.bikmukhametov.feature.profile.impl.presentation.screen

data class ProfileUiState(
    val userName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val photoUrl: String? = null,
    val tokens: String? = null,
    val isDarkTheme: Boolean = false,
    val isLoadingProfile: Boolean = false,
    val isUploadingPhoto: Boolean = false,
    val isSigningOut: Boolean = false,
)
