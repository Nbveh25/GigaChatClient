package ru.kazan.itis.bikmukhametov.feature.profile.impl.data.repository

import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.tasks.await
import ru.kazan.itis.bikmukhametov.api.model.TokensCountModel
import ru.kazan.itis.bikmukhametov.api.model.UserModel
import ru.kazan.itis.bikmukhametov.api.repository.ProfileRepository
import ru.kazan.itis.bikmukhametov.api.upload.AvatarUploader
import ru.kazan.itis.bikmukhametov.common.util.error.runCatchingCancelable
import ru.kazan.itis.bikmukhametov.common.util.resource.StringResourceProvider
import ru.kazan.itis.bikmukhametov.feature.profile.impl.R
import ru.kazan.itis.bikmukhametov.feature.profile.impl.data.api.BalanceDto
import ru.kazan.itis.bikmukhametov.feature.profile.impl.data.api.GigaChatTokensApi
import java.io.InputStream

@Singleton
internal class ProfileRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val avatarUploader: AvatarUploader,
    private val gigaChatTokensApi: GigaChatTokensApi,
    private val stringResources: StringResourceProvider,
) : ProfileRepository {

    override suspend fun getUserProfile(): Result<UserModel> = runCatchingCancelable {
        val user = firebaseAuth.currentUser
            ?: error(stringResources.getString(R.string.profile_repo_error_not_signed_in))

        UserModel(
            uid = user.uid,
            name = user.displayName,
            email = user.email,
            phone = user.phoneNumber,
            photoUrl = user.photoUrl?.toString(),
        )
    }

    override suspend fun updateUserName(name: String): Result<Unit> = runCatchingCancelable {
        val user = firebaseAuth.currentUser
            ?: error(stringResources.getString(R.string.profile_repo_error_not_signed_in))

        val profileUpdate = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()

        user.updateProfile(profileUpdate).await()
    }

    override suspend fun uploadProfilePhoto(
        inputStream: InputStream,
        fileName: String,
    ): Result<String> = runCatchingCancelable {
        val user = firebaseAuth.currentUser
            ?: error(stringResources.getString(R.string.profile_repo_error_not_signed_in))

        val photoUrl = avatarUploader.uploadAvatar(
            inputStream = inputStream,
            fileName = fileName,
            userId = user.uid,
        ).getOrThrow()

        val profileUpdate = UserProfileChangeRequest.Builder()
            .setPhotoUri(photoUrl.toUri())
            .build()

        user.updateProfile(profileUpdate).await()

        photoUrl
    }

    override suspend fun getTokensBalance(): Result<TokensCountModel> = runCatchingCancelable {
        val response = gigaChatTokensApi.getTokensBalance()

        val balanceValue = extractBalance(response.balance)
            ?: error(stringResources.getString(R.string.profile_repo_error_tokens_balance_invalid))

        TokensCountModel(tokens = balanceValue)
    }

    private fun extractBalance(items: List<BalanceDto>): Int? {
        val rawValue = items.find { it.usage.equals(GIGACHAT_USAGE, ignoreCase = true) }?.value
            ?: items.firstOrNull()?.value
            ?: return null

        return rawValue.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
    }

    private companion object {
        private const val GIGACHAT_USAGE = "GigaChat"
    }
}
