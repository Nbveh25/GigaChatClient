package ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase

fun interface SendChatMessageUseCase {
    suspend operator fun invoke(messages: List<Pair<String, String>>): Result<String>
}
