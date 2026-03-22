package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.domain.usecase

import javax.inject.Inject
import ru.kazan.itis.bikmukhametov.database.chatlist.repository.ChatRepository
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.model.ChatMessageModel
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.GetChatMessagesUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.data.mapper.toModel

internal class GetChatMessagesUseCaseImpl @Inject constructor(
    private val chatRepository: ChatRepository,
) : GetChatMessagesUseCase {

    override suspend fun invoke(chatId: String): List<ChatMessageModel> =
        chatRepository.getMessagesForChat(chatId).map { it.toModel() }
}
