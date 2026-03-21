package ru.kazan.itis.bikmukhametov.feature.chatlist.api.usecase

import ru.kazan.itis.bikmukhametov.feature.chatlist.api.model.ChatModel

fun interface LoadChatsBySearchUseCase {
    suspend operator fun invoke(searchQuery: String): Result<List<ChatModel>>
}
