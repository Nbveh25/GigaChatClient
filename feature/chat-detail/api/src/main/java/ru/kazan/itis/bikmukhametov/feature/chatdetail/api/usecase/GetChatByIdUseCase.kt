package ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase

import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.model.ChatSummaryModel

fun interface GetChatByIdUseCase {
    suspend operator fun invoke(chatId: String): ChatSummaryModel?
}
