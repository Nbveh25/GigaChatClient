package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.domain.usecase

import javax.inject.Inject
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.SendChatMessageUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.repository.GigaChatMessagesRepository

internal class SendChatMessageUseCaseImpl @Inject constructor(
    private val gigaChatMessagesRepository: GigaChatMessagesRepository,
) : SendChatMessageUseCase {

    override suspend fun invoke(messages: List<Pair<String, String>>): Result<String> =
        gigaChatMessagesRepository.sendChatCompletion(messages)
}
