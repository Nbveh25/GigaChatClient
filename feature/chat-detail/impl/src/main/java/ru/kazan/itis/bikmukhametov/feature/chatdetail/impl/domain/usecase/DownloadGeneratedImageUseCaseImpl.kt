package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.domain.usecase

import javax.inject.Inject
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.repository.GigaChatMessagesRepository
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.DownloadGeneratedImageUseCase

internal class DownloadGeneratedImageUseCaseImpl @Inject constructor(
    private val gigaChatMessagesRepository: GigaChatMessagesRepository,
) : DownloadGeneratedImageUseCase {

    override suspend fun invoke(fileId: String): Result<ByteArray> =
        gigaChatMessagesRepository.downloadGeneratedImage(fileId)
}
