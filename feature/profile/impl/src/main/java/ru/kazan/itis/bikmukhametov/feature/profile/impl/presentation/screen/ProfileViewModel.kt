package ru.kazan.itis.bikmukhametov.feature.profile.impl.presentation.screen

import androidx.lifecycle.viewModelScope
import android.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.kazan.itis.bikmukhametov.api.usecase.GetTokensCountUseCase
import ru.kazan.itis.bikmukhametov.api.usecase.GetUserProfileUseCase
import ru.kazan.itis.bikmukhametov.api.usecase.SelectImageUseCase
import ru.kazan.itis.bikmukhametov.api.usecase.SignOutUseCase
import ru.kazan.itis.bikmukhametov.api.usecase.UpdateUserNameUseCase
import ru.kazan.itis.bikmukhametov.api.usecase.UploadProfilePhotoUseCase
import ru.kazan.itis.bikmukhametov.common.util.viewmodel.BaseViewModel

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getTokensCountUseCase: GetTokensCountUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserNameUseCase: UpdateUserNameUseCase,
    private val selectImageUseCase: SelectImageUseCase,
    private val uploadProfilePhotoUseCase: UploadProfilePhotoUseCase,
    private val signOutUseCase: SignOutUseCase,
) : BaseViewModel<ProfileUiState, ProfileIntent>(ProfileUiState()) {

    private val _effect = MutableSharedFlow<ProfileEffect>(extraBufferCapacity = 64)
    val effect: SharedFlow<ProfileEffect> = _effect.asSharedFlow()

    init {
        onIntent(ProfileIntent.LoadProfile)
    }

    override fun onIntent(action: ProfileIntent) {
        when (action) {
            ProfileIntent.LoadProfile -> loadProfile()
            is ProfileIntent.UpdateUserName -> updateUserName(action.name)
            ProfileIntent.PhotoClicked -> emitEffect(ProfileEffect.OpenPhotoPicker)
            is ProfileIntent.PhotoSelected -> uploadPhoto(action.imageUriString)
            is ProfileIntent.ThemeChanged -> updateState { copy(isDarkTheme = action.isDarkTheme) }
            ProfileIntent.SignOutClicked -> signOut()
        }
    }

    private fun updateUserName(name: String) {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty() || state.value.isUpdatingUserName) return

        viewModelScope.launch {
            updateState { copy(isUpdatingUserName = true) }
            updateUserNameUseCase(trimmedName)
                .onSuccess {
                    updateState { copy(isUpdatingUserName = false) }
                    loadProfile()
                }
                .onFailure { error ->
                    updateState { copy(isUpdatingUserName = false) }
                    emitEffect(
                        ProfileEffect.ShowError(
                            error.message ?: "Не удалось обновить имя пользователя",
                        ),
                    )
                }
        }
    }

    private fun loadProfile() {
        if (state.value.isLoadingProfile) return

        viewModelScope.launch {
            updateState { copy(isLoadingProfile = true) }
            getUserProfileUseCase()
                .onSuccess { user ->
                    updateState {
                        copy(
                            userName = user.name,
                            email = user.email,
                            phone = user.phone,
                            photoUrl = user.photoUrl,
                            isLoadingProfile = false,
                        )
                    }
                    loadTokensCount()
                }
                .onFailure { error ->
                    updateState { copy(isLoadingProfile = false) }
                    emitEffect(
                        ProfileEffect.ShowError(
                            error.message ?: "Не удалось загрузить профиль",
                        ),
                    )
                }
        }
    }

    private fun loadTokensCount() {
        viewModelScope.launch {
            Log.d(TAG, "loadTokensCount: start")
            getTokensCountUseCase()
                .onSuccess { count ->
                    Log.d(TAG, "loadTokensCount: success tokens=${count.tokens}")
                    updateState { copy(tokens = count.tokens.toString()) }
                }
                .onFailure { error ->
                    Log.e(TAG, "loadTokensCount: failure ${error.javaClass.simpleName}: ${error.message}", error)
                    updateState { copy(tokens = null) }
                    emitEffect(
                        ProfileEffect.ShowError(
                            error.message ?: "Не удалось загрузить баланс токенов",
                        ),
                    )
                }
        }
    }

    private fun uploadPhoto(imageUriString: String) {
        if (state.value.isUploadingPhoto) return

        viewModelScope.launch {
            updateState { copy(isUploadingPhoto = true) }

            selectImageUseCase(imageUriString)
                .mapCatching { image ->
                    uploadProfilePhotoUseCase(
                        inputStream = image.bytes.inputStream(),
                        fileName = image.fileName,
                    ).getOrThrow()
                }
                .onSuccess {
                    updateState { copy(isUploadingPhoto = false) }
                    loadProfile()
                }
                .onFailure { error ->
                    updateState { copy(isUploadingPhoto = false) }
                    emitEffect(
                        ProfileEffect.ShowError(
                            error.message ?: "Не удалось обновить фото профиля",
                        ),
                    )
                }
        }
    }

    private fun signOut() {
        if (state.value.isSigningOut) return

        viewModelScope.launch {
            updateState { copy(isSigningOut = true) }
            signOutUseCase()
            updateState { copy(isSigningOut = false) }
            _effect.emit(ProfileEffect.SignedOut)
        }
    }

    private fun emitEffect(effect: ProfileEffect) {
        viewModelScope.launch { _effect.emit(effect) }
    }

    private companion object {
        private const val TAG = "ProfileViewModel"
    }
}
