package ru.kazan.itis.bikmukhametov.feature.chatlist.api.usecase

import ru.kazan.itis.bikmukhametov.feature.chatlist.api.model.ChatModel

fun interface LoadChatsUseCase {
    suspend operator fun invoke(offset: Int, limit: Int): Result<List<ChatModel>>
}
