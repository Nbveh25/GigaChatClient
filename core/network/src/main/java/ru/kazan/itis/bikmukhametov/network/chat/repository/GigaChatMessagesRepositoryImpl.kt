package ru.kazan.itis.bikmukhametov.network.chat.repository

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException
import retrofit2.HttpException
import ru.kazan.itis.bikmukhametov.common.util.error.runCatchingCancelable
import ru.kazan.itis.bikmukhametov.network.chat.api.ChatMessageDto
import ru.kazan.itis.bikmukhametov.network.chat.api.ChatRequest
import ru.kazan.itis.bikmukhametov.network.chat.api.GigaChatMainApi
import timber.log.Timber
import java.io.IOException

@Singleton
class GigaChatMessagesRepositoryImpl @Inject constructor(
    private val chatApi: GigaChatMainApi,
) : GigaChatMessagesRepository {

    override suspend fun sendChatCompletion(messages: List<Pair<String, String>>): Result<String> {

        if (messages.isEmpty()) {

            Timber.w("GigaChatMessagesRepository: messages empty")

            return Result.failure(IllegalArgumentException("Пустой список сообщений"))
        }

        val request = ChatRequest(
            model = ChatRequest.DEFAULT_MODEL,
            messages = messages.toDtoList()
        )

        Timber.d("GigaChatMessagesRepository: messagesCount=${messages.size}, model=${request.model}")

        return runCatchingCancelable {
            chatApi.sendMessage(request)
        }.mapCatching { response ->

            response.choices.firstOrNull()?.message?.content?.takeIf { it.isNotEmpty() }
                ?: error("Пустой ответ от API")

        }.onFailure { exception ->

            val logMessage = when (exception) {
                is HttpException -> "HTTP ${exception.code()}: ${exception.response()?.errorBody()?.string()}"
                else -> exception.message ?: "Unknown error"
            }

            Timber.e("GigaChatMessagesRepository: $logMessage")
        }

    }

    private fun List<Pair<String, String>>.toDtoList() = map { (role, content) ->
        ChatMessageDto(role = role, content = content)
    }
}
