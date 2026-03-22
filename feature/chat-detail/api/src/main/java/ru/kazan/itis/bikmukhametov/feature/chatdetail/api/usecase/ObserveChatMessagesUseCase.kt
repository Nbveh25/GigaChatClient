package ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase

import kotlinx.coroutines.flow.Flow
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.model.ChatMessageModel

fun interface ObserveChatMessagesUseCase {
    operator fun invoke(chatId: String): Flow<List<ChatMessageModel>>
}
