package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.presentation.screen

object ChatDetailMocks {
    fun defaultChatTitle(chatId: String): String =
        if (chatId.isNotBlank()) "Чат $chatId" else "Новый чат"

    fun initialState(chatTitle: String): ChatDetailUiState =
        ChatDetailUiState(
            chatTitle = chatTitle,
            messages = listOf(
                ChatMessageUi(role = ChatMessageRole.User, text = "Привет!"),
                ChatMessageUi(
                    role = ChatMessageRole.Assistant,
                    text = "Здравствуйте! Я ассистент на базе GigaChat. Это демо-экран без вызова API.",
                ),
            ),
            inputText = "",
            isGenerating = false,
            generationError = false,
            pendingRetryText = null,
        )

    fun mockAssistantReply(userMessage: String): String {
        val preview = userMessage.trim().take(500)
        return "Ответ (мок): «$preview»"
    }
}
