package ru.kazan.itis.bikmukhametov.feature.chatlist.api.usecase

import kotlinx.coroutines.flow.Flow
import ru.kazan.itis.bikmukhametov.feature.chatlist.api.model.ChatModel

fun interface ObserveChatsUseCase {
    operator fun invoke(): Flow<List<ChatModel>>
}
