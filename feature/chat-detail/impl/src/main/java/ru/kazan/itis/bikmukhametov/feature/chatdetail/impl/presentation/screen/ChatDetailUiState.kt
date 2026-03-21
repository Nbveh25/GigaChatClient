package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.presentation.screen

import java.util.UUID

enum class ChatMessageRole {
    User,
    Assistant,
}

data class ChatMessageUi(
    val id: String = UUID.randomUUID().toString(),
    val role: ChatMessageRole,
    val text: String,
)

data class ChatDetailUiState(
    val chatTitle: String,
    val messages: List<ChatMessageUi>,
    val inputText: String,
    val isGenerating: Boolean,
    val generationError: Boolean,
    val pendingRetryText: String?,
)
