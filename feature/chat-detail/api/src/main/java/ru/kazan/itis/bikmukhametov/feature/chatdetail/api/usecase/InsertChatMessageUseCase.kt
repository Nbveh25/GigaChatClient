package ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase

import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.model.ChatMessageModel

fun interface InsertChatMessageUseCase {
    suspend operator fun invoke(message: ChatMessageModel)
}
