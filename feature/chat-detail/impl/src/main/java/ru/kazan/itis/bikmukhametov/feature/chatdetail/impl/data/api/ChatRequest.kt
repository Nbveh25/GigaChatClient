package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.data.api

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageDto(
    val role: String,
    val content: String,
)

@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<ChatMessageDto>,
    val stream: Boolean = false,
) {
    companion object {
        const val DEFAULT_MODEL = "GigaChat-2"
    }
}
