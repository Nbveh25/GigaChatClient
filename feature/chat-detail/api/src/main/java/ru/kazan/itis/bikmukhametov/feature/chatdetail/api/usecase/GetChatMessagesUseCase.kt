package ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase

import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.model.ChatMessageModel

fun interface GetChatMessagesUseCase {
    suspend operator fun invoke(chatId: String): List<ChatMessageModel>
}
