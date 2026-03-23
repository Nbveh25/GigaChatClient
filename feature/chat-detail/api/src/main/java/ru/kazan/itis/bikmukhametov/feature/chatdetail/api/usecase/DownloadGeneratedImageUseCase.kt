package ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase

fun interface DownloadGeneratedImageUseCase {
    suspend operator fun invoke(fileId: String): Result<ByteArray>
}
