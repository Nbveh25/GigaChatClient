package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.data.api

import kotlinx.serialization.Serializable

@Serializable
data class ChatResponse(
    val choices: List<ChatChoice>,
)

@Serializable
data class ChatChoice(
    val message: ChatMessageResponse,
)

@Serializable
data class ChatMessageResponse(
    val content: String = "",
    val role: String = "assistant",
)
