package ru.kazan.itis.bikmukhametov.feature.chatlist.api.usecase

import ru.kazan.itis.bikmukhametov.feature.chatlist.api.model.ChatModel

interface SearchChatUseCase {
    suspend operator fun invoke(query: String): List<ChatModel>
}
