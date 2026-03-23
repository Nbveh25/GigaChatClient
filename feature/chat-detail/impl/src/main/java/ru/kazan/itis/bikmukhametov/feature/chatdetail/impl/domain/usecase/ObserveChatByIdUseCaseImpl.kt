package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.domain.usecase

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.kazan.itis.bikmukhametov.database.chatlist.repository.ChatRepository
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.model.ChatSummaryModel
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.ObserveChatByIdUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.data.mapper.toSummaryModel

class ObserveChatByIdUseCaseImpl @Inject constructor(
    private val chatRepository: ChatRepository,
) : ObserveChatByIdUseCase {

    override fun invoke(chatId: String): Flow<ChatSummaryModel?> =
        chatRepository.observeChatById(chatId).map { entity -> entity?.toSummaryModel() }
}
