package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.data.repository

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException
import retrofit2.HttpException
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.repository.GigaChatMessagesRepository
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.data.api.ChatMessageDto
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.data.api.ChatRequest
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.data.api.GigaChatMainApi
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
            messages = messages.map { (role, content) -> ChatMessageDto(role = role, content = content) },
        )

        Timber.d("GigaChatMessagesRepository: messagesCount=%d, model=%s", messages.size, request.model)

        return try {
            val response = chatApi.sendMessage(request)
            val content = response.choices.firstOrNull()?.message?.content
            if (content.isNullOrEmpty()) {
                Timber.w("GigaChatMessagesRepository: empty response from API")
                Result.failure(IllegalStateException("Пустой ответ от API"))
            } else {
                Timber.d("GigaChatMessagesRepository: success, contentLength=%d", content.length)
                Result.success(content)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            val body = e.response()?.errorBody()?.string() ?: "(пусто)"
            Timber.e("GigaChatMessagesRepository: HTTP %d | %s | body=%s", e.code(), e.message(), body)
            Result.failure(IOException("HTTP ${e.code()}: $body"))
        } catch (e: Throwable) {
            Timber.e(e, "GigaChatMessagesRepository: unexpected error: %s", e.message)
            Result.failure(e)
        }
    }
}
