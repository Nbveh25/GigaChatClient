package ru.kazan.itis.bikmukhametov.feature.chatlist.impl.domain.usecase

import javax.inject.Inject
import ru.kazan.itis.bikmukhametov.common.util.error.runCatchingCancelable
import ru.kazan.itis.bikmukhametov.database.chatlist.repository.ChatRepository
import ru.kazan.itis.bikmukhametov.feature.chatlist.api.usecase.CreateChatUseCase

class CreateChatUseCaseImpl @Inject constructor(
    private val chatRepository: ChatRepository,
) : CreateChatUseCase {

    override suspend fun invoke(): Result<String> = runCatchingCancelable {
        chatRepository.createChat()
    }
}
