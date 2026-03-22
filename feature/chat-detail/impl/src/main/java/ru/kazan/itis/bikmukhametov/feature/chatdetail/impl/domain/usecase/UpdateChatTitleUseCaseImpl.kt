package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.domain.usecase

import javax.inject.Inject
import ru.kazan.itis.bikmukhametov.database.chatlist.repository.ChatRepository
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.UpdateChatTitleUseCase

internal class UpdateChatTitleUseCaseImpl @Inject constructor(
    private val chatRepository: ChatRepository,
) : UpdateChatTitleUseCase {

    override suspend fun invoke(chatId: String, title: String) {
        chatRepository.updateChatTitle(chatId, title)
    }
}
