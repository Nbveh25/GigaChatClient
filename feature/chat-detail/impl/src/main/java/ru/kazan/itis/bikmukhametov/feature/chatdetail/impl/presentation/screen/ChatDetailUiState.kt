package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.presentation.screen

import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.presentation.item.ChatMessageItem

data class ChatDetailUiState(
    val chatTitle: String = "Новый чат",
    val messages: List<ChatMessageItem> = emptyList(),
    val inputText: String = "",
    val imageGenerationEnabled: Boolean = false,
    val isGenerating: Boolean = false,
    val generationError: Boolean = false,
    val generationErrorMessage: String? = null,
    val pendingRetryText: String? = null,
)
