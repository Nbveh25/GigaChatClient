package ru.kazan.itis.bikmukhametov.feature.chatlist.impl.domain.usecase

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.kazan.itis.bikmukhametov.database.chatlist.repository.ChatRepository
import ru.kazan.itis.bikmukhametov.feature.chatlist.api.model.ChatModel
import ru.kazan.itis.bikmukhametov.feature.chatlist.api.usecase.ObserveChatsUseCase
import ru.kazan.itis.bikmukhametov.feature.chatlist.impl.data.mapper.asChatModel

class ObserveChatsUseCaseImpl @Inject constructor(
    private val chatRepository: ChatRepository,
) : ObserveChatsUseCase {

    override fun invoke(): Flow<List<ChatModel>> =
        chatRepository.observeChats().map { entities -> entities.map { it.asChatModel() } }
}
