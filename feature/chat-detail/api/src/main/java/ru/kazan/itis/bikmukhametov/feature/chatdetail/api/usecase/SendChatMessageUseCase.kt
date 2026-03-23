package ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase

import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.model.ChatAssistantReply
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.model.ChatCompletionMessage

fun interface SendChatMessageUseCase {
    suspend operator fun invoke(messages: List<ChatCompletionMessage>): Result<ChatAssistantReply>
}
