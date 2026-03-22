package ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase

fun interface UpdateChatTitleUseCase {
    suspend operator fun invoke(chatId: String, title: String)
}
