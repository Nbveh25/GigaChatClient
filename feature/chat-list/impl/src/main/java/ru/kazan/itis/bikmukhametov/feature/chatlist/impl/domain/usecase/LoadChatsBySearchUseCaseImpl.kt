package ru.kazan.itis.bikmukhametov.feature.chatlist.impl.domain.usecase

import ru.kazan.itis.bikmukhametov.common.util.error.runCatchingCancelable
import javax.inject.Inject
import ru.kazan.itis.bikmukhametov.database.chatlist.repository.ChatRepository
import ru.kazan.itis.bikmukhametov.feature.chatlist.api.model.ChatModel
import ru.kazan.itis.bikmukhametov.feature.chatlist.api.usecase.LoadChatsBySearchUseCase
import ru.kazan.itis.bikmukhametov.feature.chatlist.impl.data.mapper.asChatModel

class LoadChatsBySearchUseCaseImpl @Inject constructor(
    private val chatRepository: ChatRepository,
) : LoadChatsBySearchUseCase {

    override suspend fun invoke(searchQuery: String): Result<List<ChatModel>> =
        runCatchingCancelable {
            val trimmed = searchQuery.trim()
            require(trimmed.isNotEmpty()) { "searchQuery must not be blank" }
            chatRepository.getChatsPageByTitle(trimmed).map { it.asChatModel() }
        }
}
