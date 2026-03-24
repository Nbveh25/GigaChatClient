package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.data.repository

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException
import retrofit2.HttpException
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.model.ChatAssistantReply
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.model.ChatCompletionMessage
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.repository.GigaChatMessagesRepository
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.data.api.ChatBuiltinFunction
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.data.api.ChatMessageDto
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.data.api.ChatRequest
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.data.api.GigaChatMainApi
import timber.log.Timber
import java.io.IOException

@Singleton
class GigaChatMessagesRepositoryImpl @Inject constructor(
    private val chatApi: GigaChatMainApi,
) : GigaChatMessagesRepository {

    override suspend fun sendChatCompletion(
        messages: List<ChatCompletionMessage>,
        imageGenerationEnabled: Boolean,
    ): Result<ChatAssistantReply> {
        if (messages.isEmpty()) {
            Timber.w("GigaChatMessagesRepository: messages empty")
            return Result.failure(IllegalArgumentException("Пустой список сообщений"))
        }

        val messageDtos = messages.map { m ->
            ChatMessageDto(
                role = m.role,
                content = m.content,
                functionsStateId = m.functionsStateId?.takeIf { m.role == ASSISTANT_ROLE },
            )
        }
        val lastUserMessage = messages.lastOrNull { it.role == USER_ROLE }?.content.orEmpty()

        val request = ChatRequest(
            model = ChatRequest.DEFAULT_MODEL,
            messages = messageDtos,
            stream = false,
            functionCall = "auto",
            functions = if (imageGenerationEnabled) {
                listOf(ChatBuiltinFunction(name = "text2image"))
            } else {
                null
            },
        )

        Timber.d("GigaChatMessagesRepository: messagesCount=%d, model=%s", messages.size, request.model)

        return try {
            val response = chatApi.sendMessage(request)
            val message = response.choices.firstOrNull()?.message
            val content = message?.content
            if (content.isNullOrEmpty()) {
                Timber.w("GigaChatMessagesRepository: empty response from API")
                Result.failure(IllegalStateException("Пустой ответ от API"))
            } else {
                val reply = ChatAssistantReply(
                    content = content,
                    functionsStateId = message.functionsStateId,
                )
                val shouldForceImageRetry = imageGenerationEnabled && !containsImageTag(content)

                if (shouldForceImageRetry) {
                    Timber.w("GigaChatMessagesRepository: no <img> for image prompt, retrying with forced system prompt")
                    val forcedImageResult = requestImageWithForcedSystemPrompt(lastUserMessage)
                    forcedImageResult.getOrNull()?.let { forced ->
                        if (containsImageTag(forced.content)) {
                            return Result.success(forced)
                        }
                    }
                }

                Timber.d("GigaChatMessagesRepository: success, contentLength=%d", content.length)
                Result.success(reply)
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

    private suspend fun requestImageWithForcedSystemPrompt(userPrompt: String): Result<ChatAssistantReply> {
        val forcedRequest = ChatRequest(
            model = ChatRequest.DEFAULT_MODEL,
            messages = listOf(
                ChatMessageDto(
                    role = SYSTEM_ROLE,
                    content = FORCE_IMAGE_SYSTEM_PROMPT,
                ),
                ChatMessageDto(
                    role = USER_ROLE,
                    content = userPrompt,
                ),
            ),
            functionCall = "auto",
            functions = listOf(ChatBuiltinFunction(name = "text2image")),
        )

        return try {
            val message = chatApi.sendMessage(forcedRequest).choices.firstOrNull()?.message
            val content = message?.content
            if (content.isNullOrBlank()) {
                Result.failure(IllegalStateException("Пустой ответ при форсированной генерации изображения"))
            } else {
                Result.success(
                    ChatAssistantReply(
                        content = content,
                        functionsStateId = message?.functionsStateId,
                    ),
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    private fun containsImageTag(text: String): Boolean = IMAGE_TAG_REGEX.containsMatchIn(text)

    override suspend fun downloadGeneratedImage(fileId: String): Result<ByteArray> {
        if (fileId.isBlank()) {
            return Result.failure(IllegalArgumentException("Пустой id изображения"))
        }

        return try {
            val imageBytes = chatApi.downloadImage(fileId).bytes()
            if (imageBytes.isEmpty()) {
                Result.failure(IllegalStateException("Пустой файл изображения"))
            } else {
                Result.success(imageBytes)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            val body = e.response()?.errorBody()?.string() ?: "(пусто)"
            Timber.e("GigaChatMessagesRepository: image HTTP %d | body=%s", e.code(), body)
            Result.failure(IOException("HTTP ${e.code()}: $body"))
        } catch (e: Throwable) {
            Timber.e(e, "GigaChatMessagesRepository: image download error: %s", e.message)
            Result.failure(e)
        }
    }

    private companion object {
        private const val USER_ROLE = "user"
        private const val SYSTEM_ROLE = "system"
        private val IMAGE_TAG_REGEX = Regex("<img\\s+src=[\"']([^\"']+)[\"'][^>]*/?>")
        private const val FORCE_IMAGE_SYSTEM_PROMPT =
            "Если пользователь просит нарисовать или сгенерировать изображение, " +
                "обязательно вызови встроенную функцию text2image и верни результат с тегом <img src=\"...\" fuse=\"true\"/>."

        private const val ASSISTANT_ROLE = "assistant"
    }
}
