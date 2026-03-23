package ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase

import kotlinx.coroutines.flow.Flow
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.model.ChatSummaryModel

fun interface ObserveChatByIdUseCase {
    operator fun invoke(chatId: String): Flow<ChatSummaryModel?>
}
