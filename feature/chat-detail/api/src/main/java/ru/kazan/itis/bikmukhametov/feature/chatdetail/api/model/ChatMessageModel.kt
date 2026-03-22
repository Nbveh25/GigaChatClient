package ru.kazan.itis.bikmukhametov.feature.chatdetail.api.model

data class ChatMessageModel(
    val id: String,
    val chatId: String,
    val role: String,
    val text: String,
    val createdAtEpochMillis: Long,
)
