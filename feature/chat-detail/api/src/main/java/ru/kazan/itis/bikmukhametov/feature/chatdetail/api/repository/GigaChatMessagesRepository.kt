package ru.kazan.itis.bikmukhametov.feature.chatdetail.api.repository

import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.model.ChatAssistantReply
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.model.ChatCompletionMessage

interface GigaChatMessagesRepository {

    /**
     * Отправляет историю в GigaChat и возвращает ответ ассистента (в т.ч. [ChatAssistantReply.functionsStateId] для следующих запросов).
     */
    suspend fun sendChatCompletion(messages: List<ChatCompletionMessage>): Result<ChatAssistantReply>

    suspend fun downloadGeneratedImage(fileId: String): Result<ByteArray>
}
