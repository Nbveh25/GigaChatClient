package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.domain.usecase

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.kazan.itis.bikmukhametov.database.chatlist.repository.ChatRepository
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.model.ChatMessageModel
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.ObserveChatMessagesUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.data.mapper.toModel

internal class ObserveChatMessagesUseCaseImpl @Inject constructor(
    private val chatRepository: ChatRepository,
) : ObserveChatMessagesUseCase {

    override fun invoke(chatId: String): Flow<List<ChatMessageModel>> =
        chatRepository.observeMessages(chatId).map { list -> list.map { it.toModel() } }
}
