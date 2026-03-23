package ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase

interface RequestAssistantReplyUseCase {
    suspend operator fun invoke(chatId: String): Result<Unit>
}
