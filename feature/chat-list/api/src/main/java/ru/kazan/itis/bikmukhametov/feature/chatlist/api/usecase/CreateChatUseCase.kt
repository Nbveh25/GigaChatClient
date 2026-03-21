package ru.kazan.itis.bikmukhametov.feature.chatlist.api.usecase

fun interface CreateChatUseCase {
    suspend operator fun invoke(): Result<String>
}
