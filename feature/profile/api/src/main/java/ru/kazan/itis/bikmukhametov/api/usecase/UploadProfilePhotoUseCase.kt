package ru.kazan.itis.bikmukhametov.api.usecase

import java.io.InputStream

interface UploadProfilePhotoUseCase {
    suspend operator fun invoke(
        inputStream: InputStream,
        fileName: String
    ): Result<String>
}

