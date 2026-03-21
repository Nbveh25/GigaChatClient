package ru.kazan.itis.bikmukhametov.feature.chatlist.impl.domain.usecase

import ru.kazan.itis.bikmukhametov.common.util.error.runCatchingCancelable
import javax.inject.Inject
import ru.kazan.itis.bikmukhametov.database.chatlist.repository.ChatRepository
import ru.kazan.itis.bikmukhametov.feature.chatlist.api.model.ChatModel
import ru.kazan.itis.bikmukhametov.feature.chatlist.api.usecase.LoadChatsUseCase
import ru.kazan.itis.bikmukhametov.feature.chatlist.impl.data.mapper.asChatModel

class LoadChatsUseCaseImpl @Inject constructor(
    private val chatRepository: ChatRepository,
) : LoadChatsUseCase {

    override suspend fun invoke(offset: Int, limit: Int): Result<List<ChatModel>> =
        runCatchingCancelable {
            chatRepository.getChatsPage(offset, limit).map { it.asChatModel() }
        }
}
