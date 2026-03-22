package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.presentation.screen

object ChatDetailMocks {
    fun defaultChatTitle(chatId: String): String =
        if (chatId.isNotBlank()) "Чат $chatId" else "Новый чат"

}
