package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.domain.usecase

import javax.inject.Inject
import ru.kazan.itis.bikmukhametov.database.chatlist.repository.ChatRepository
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.model.ChatSummaryModel
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.GetChatByIdUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.data.mapper.toSummaryModel

internal class GetChatByIdUseCaseImpl @Inject constructor(
    private val chatRepository: ChatRepository,
) : GetChatByIdUseCase {

    override suspend fun invoke(chatId: String): ChatSummaryModel? =
        chatRepository.getChatById(chatId)?.toSummaryModel()
}
