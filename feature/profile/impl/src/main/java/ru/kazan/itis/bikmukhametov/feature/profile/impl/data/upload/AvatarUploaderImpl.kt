package ru.kazan.itis.bikmukhametov.feature.profile.impl.data.upload

import android.content.Context
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import ru.kazan.itis.bikmukhametov.api.upload.AvatarUploader
import ru.kazan.itis.bikmukhametov.feature.profile.impl.BuildConfig
import java.io.InputStream
import javax.inject.Inject
import kotlin.coroutines.resume

internal class AvatarUploaderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AvatarUploader {

    private companion object {
        const val TAG = "CloudinaryUploader"
        const val FOLDER_AVATARS = "avatars"
        const val CLOUD_NAME = "dsrqq4er2"
        const val IMAGE_RESOURCE_TYPE = "image"
    }

    init {
        setupCloudinary()
    }

    override suspend fun uploadAvatar(
        inputStream: InputStream,
        fileName: String,
        userId: String
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val bytes = inputStream.use { it.readBytes() }

            uploadToCloudinary(bytes, userId)
        }.onFailure {
            Log.e(TAG, "Upload failed", it)
        }
    }

    private suspend fun uploadToCloudinary(bytes: ByteArray, userId: String): String =
        suspendCancellableCoroutine { continuation ->
            MediaManager.get().upload(bytes)
                .options(uploadOptions(userId))
                .callback(object : DefaultUploadCallback() {
                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val url = resultData["secure_url"] as? String ?: ""
                        continuation.resume(url)
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        continuation.resumeWith(Result.failure(Exception(error.description)))
                    }
                })
                .dispatch()
        }

    private fun uploadOptions(userId: String) = mapOf(
        "public_id" to userId,
        "folder" to FOLDER_AVATARS,
        "api_key" to BuildConfig.CLOUDINARY_API_KEY,
        "api_secret" to BuildConfig.CLOUDINARY_API_SECRET,
        "overwrite" to true,
        "resource_type" to IMAGE_RESOURCE_TYPE
    )

    private fun setupCloudinary() {
        runCatching {
            MediaManager.init(context, mapOf("cloud_name" to CLOUD_NAME, "secure" to true))
        }
    }

}
