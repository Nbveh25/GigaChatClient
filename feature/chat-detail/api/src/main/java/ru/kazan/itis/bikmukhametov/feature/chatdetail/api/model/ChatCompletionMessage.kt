package ru.kazan.itis.bikmukhametov.feature.chatdetail.api.model

/**
 * Сообщение для [ru.kazan.itis.bikmukhametov.feature.chatdetail.api.repository.GigaChatMessagesRepository.sendChatCompletion].
 * Для ассистента при многоходовом диалоге с функциями нужно передавать [functionsStateId] из ответа API.
 */
data class ChatCompletionMessage(
    val role: String,
    val content: String,
    val functionsStateId: String? = null,
)
