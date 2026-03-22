package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.domain.usecase

import javax.inject.Inject
import ru.kazan.itis.bikmukhametov.database.chatlist.repository.ChatRepository
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.model.ChatMessageModel
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.InsertChatMessageUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.data.mapper.toEntity

internal class InsertChatMessageUseCaseImpl @Inject constructor(
    private val chatRepository: ChatRepository,
) : InsertChatMessageUseCase {

    override suspend fun invoke(message: ChatMessageModel) {
        chatRepository.insertMessage(message.toEntity())
    }
}
