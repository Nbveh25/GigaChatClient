package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.data.repository

import javax.inject.Inject
import javax.inject.Singleton
import ru.kazan.itis.bikmukhametov.common.util.error.runCatchingCancelable
import ru.kazan.itis.bikmukhametov.common.util.resource.StringResourceProvider
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.model.ChatAssistantReply
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.model.ChatCompletionMessage
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.repository.GigaChatMessagesRepository
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.R
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.data.api.ChatBuiltinFunction
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.data.api.ChatMessageDto
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.data.api.ChatRequest
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.data.api.GigaChatMainApi

@Singleton
class GigaChatMessagesRepositoryImpl @Inject constructor(
    private val chatApi: GigaChatMainApi,
    private val stringResources: StringResourceProvider,
) : GigaChatMessagesRepository {

    private suspend fun requestImageWithForcedSystemPrompt(userPrompt: String): Result<ChatAssistantReply> =
        runCatchingCancelable {
            val forcedRequest = ChatRequest(
                model = ChatRequest.DEFAULT_MODEL,
                messages = listOf(
                    ChatMessageDto(
                        role = SYSTEM_ROLE,
                        content = stringResources.getString(R.string.chat_detail_repo_system_prompt_force_image),
                    ),
                    ChatMessageDto(role = USER_ROLE, content = userPrompt),
                ),
                functionCall = AUTO_FUNCTION_CALL,
                functions = listOf(ChatBuiltinFunction(name = TEXT_2_IMAGE_FUNCTION_NAME)),
            )

            val message = chatApi.sendMessage(forcedRequest).choices.firstOrNull()?.message
            val content = message?.content
                ?: error(stringResources.getString(R.string.chat_detail_repo_error_empty_forced_image))

            ChatAssistantReply(content = content, functionsStateId = message.functionsStateId)
        }

    override suspend fun downloadGeneratedImage(fileId: String): Result<ByteArray> =
        runCatchingCancelable {
            require(fileId.isNotBlank()) {
                stringResources.getString(R.string.chat_detail_repo_error_empty_file_id)
            }
            val bytes = chatApi.downloadImage(fileId).bytes()
            if (bytes.isEmpty()) {
                error(stringResources.getString(R.string.chat_detail_repo_error_empty_image_bytes))
            }
            bytes
        }

    override suspend fun sendChatCompletion(
        messages: List<ChatCompletionMessage>,
        imageGenerationEnabled: Boolean,
    ): Result<ChatAssistantReply> = runCatchingCancelable {
        require(messages.isNotEmpty()) {
            stringResources.getString(R.string.chat_detail_repo_error_empty_messages)
        }

        val request = createChatRequest(messages, imageGenerationEnabled)
        val response = chatApi.sendMessage(request)
        val choice = response.choices.firstOrNull()?.message
            ?: error(stringResources.getString(R.string.chat_detail_repo_error_empty_response))

        val content = choice.content
        val reply = ChatAssistantReply(
            content = content,
            functionsStateId = choice.functionsStateId,
        )

        if (imageGenerationEnabled && !containsImageTag(content)) {
            val lastUserPrompt = messages.lastOrNull { it.role == USER_ROLE }?.content.orEmpty()
            requestImageWithForcedSystemPrompt(lastUserPrompt)
                .getOrNull()
                ?.takeIf { containsImageTag(it.content) }
                ?.let { return@runCatchingCancelable it }
        }

        reply
    }

    private fun createChatRequest(
        messages: List<ChatCompletionMessage>,
        imageGenerationEnabled: Boolean,
    ) = ChatRequest(
        model = ChatRequest.DEFAULT_MODEL,
        messages = messages.map { msg ->
            ChatMessageDto(
                role = msg.role,
                content = msg.content,
                functionsStateId = msg.functionsStateId?.takeIf { msg.role == ASSISTANT_ROLE },
            )
        },
        stream = false,
        functionCall = AUTO_FUNCTION_CALL,
        functions = if (imageGenerationEnabled) {
            listOf(ChatBuiltinFunction(name = TEXT_2_IMAGE_FUNCTION_NAME))
        } else {
            null
        },
    )

    private fun containsImageTag(text: String): Boolean = IMAGE_TAG_REGEX.containsMatchIn(text)

    private companion object {
        private val IMAGE_TAG_REGEX = Regex("<img\\s+src=[\"']([^\"']+)[\"'][^>]*/?>")
        private const val AUTO_FUNCTION_CALL = "auto"
        private const val TEXT_2_IMAGE_FUNCTION_NAME = "text2image"

        // Сделать энам и конвертер для бд
        private const val USER_ROLE = "user"
        private const val SYSTEM_ROLE = "system"
        private const val ASSISTANT_ROLE = "assistant"
    }
}
