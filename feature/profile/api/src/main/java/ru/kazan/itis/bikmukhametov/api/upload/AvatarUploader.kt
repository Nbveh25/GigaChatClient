package ru.kazan.itis.bikmukhametov.api.upload

import java.io.InputStream

interface AvatarUploader {
    suspend fun uploadAvatar(
        inputStream: InputStream,
        fileName: String,
        userId: String
    ): Result<String>
}