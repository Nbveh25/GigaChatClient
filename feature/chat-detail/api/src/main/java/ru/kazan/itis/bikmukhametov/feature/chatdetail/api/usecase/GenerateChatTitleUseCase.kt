package ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase

interface GenerateChatTitleUseCase {
    operator fun invoke(assistantText: String, currentTitle: String): String
}
