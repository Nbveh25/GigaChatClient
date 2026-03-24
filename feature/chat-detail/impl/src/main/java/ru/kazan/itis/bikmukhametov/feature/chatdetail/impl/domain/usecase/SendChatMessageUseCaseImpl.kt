package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.domain.usecase

import javax.inject.Inject
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.model.ChatAssistantReply
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.model.ChatCompletionMessage
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.repository.GigaChatMessagesRepository
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.SendChatMessageUseCase

internal class SendChatMessageUseCaseImpl @Inject constructor(
    private val gigaChatMessagesRepository: GigaChatMessagesRepository,
) : SendChatMessageUseCase {

    override suspend fun invoke(
        messages: List<ChatCompletionMessage>,
        imageGenerationEnabled: Boolean,
    ): Result<ChatAssistantReply> =
        gigaChatMessagesRepository.sendChatCompletion(messages, imageGenerationEnabled)
}
