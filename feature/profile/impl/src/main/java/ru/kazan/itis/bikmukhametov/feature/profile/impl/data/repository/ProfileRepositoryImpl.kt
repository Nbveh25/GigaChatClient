package ru.kazan.itis.bikmukhametov.feature.profile.impl.data.repository

import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.tasks.await
import ru.kazan.itis.bikmukhametov.api.model.UserModel
import ru.kazan.itis.bikmukhametov.api.model.TokensCountModel
import ru.kazan.itis.bikmukhametov.api.repository.ProfileRepository
import ru.kazan.itis.bikmukhametov.api.upload.AvatarUploader
import ru.kazan.itis.bikmukhametov.feature.profile.impl.data.api.BalanceDto
import ru.kazan.itis.bikmukhametov.feature.profile.impl.data.api.GigaChatTokensApi
import kotlin.math.min
import java.io.InputStream

@Singleton
internal class ProfileRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val avatarUploader: AvatarUploader,
    private val gigaChatTokensApi: GigaChatTokensApi,
) : ProfileRepository {

    override suspend fun getUserProfile(): Result<UserModel> {
        return try {
            val user = firebaseAuth.currentUser
            if (user == null) {
                Result.failure(Exception("Пользователь не авторизован"))
            } else {
                val profile = UserModel(
                    uid = user.uid,
                    name = user.displayName,
                    email = user.email,
                    phone = user.phoneNumber,
                    photoUrl = user.photoUrl?.toString()
                )
                Result.success(profile)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserName(name: String): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
            if (user == null) {
                Result.failure(Exception("Пользователь не авторизован"))
            } else {
                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                user.updateProfile(profileUpdate).await()
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadProfilePhoto(
        inputStream: InputStream,
        fileName: String
    ): Result<String> {
        return try {
            val user = firebaseAuth.currentUser
            if (user == null) {
                Result.failure(Exception("Пользователь не авторизован"))
            } else {

                val uploadResult = avatarUploader.uploadAvatar(
                    inputStream = inputStream,
                    fileName = fileName,
                    userId = user.uid
                )

                Log.d("ProfileRepositoryImpl", "URL загруженного файла: ${uploadResult.getOrNull()}")

                if (uploadResult.isFailure) {
                    return uploadResult
                }

                val photoUrl = uploadResult.getOrNull() ?: return Result.failure(
                    Exception("Не удалось получить URL загруженного файла")
                )

                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setPhotoUri(photoUrl.toUri())
                    .build()
                user.updateProfile(profileUpdate).await()

                Result.success(photoUrl)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTokensBalance(): Result<TokensCountModel> {
        return try {
            Log.d(TAG, "getTokensBalance: start request /api/v1/balance")
            val response = gigaChatTokensApi.getTokensBalance()
            Log.d(TAG, "getTokensBalance: response balance items count=${response.balance.size}")
            response.balance.forEachIndexed { index, item ->
                Log.d(TAG, "getTokensBalance: item[$index] usage=${item.usage} value=${item.value}")
            }

            val balance = extractBalance(response.balance)
                ?: return Result.failure(Exception("Не удалось получить баланс токенов: пустой или некорректный balance"))

            Log.d(TAG, "getTokensBalance: selected balance=$balance")

            Result.success(
                TokensCountModel(
                    tokens = balance,
                ),
            )
        } catch (e: Exception) {
            Log.e(TAG, "getTokensBalance: failed with ${e.javaClass.simpleName}: ${e.message}", e)
            Result.failure(e)
        }
    }

    private fun extractBalance(items: List<BalanceDto>): Int? {
        val value = items.firstOrNull { it.usage.equals(GIGACHAT_USAGE, ignoreCase = true) }?.value
            ?: items.firstOrNull()?.value
            ?: return null

        return min(value, Int.MAX_VALUE.toLong()).toInt()
    }

    private companion object {
        private const val TAG = "ProfileRepositoryImpl"
        private const val GIGACHAT_USAGE = "GigaChat"
    }
}
