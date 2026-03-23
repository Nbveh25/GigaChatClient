package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.flow.first
import ru.kazan.itis.bikmukhametov.common.util.error.runCatchingCancelable
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.model.ChatCompletionMessage
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.model.ChatMessageModel
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.GenerateChatTitleUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.ObserveChatByIdUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.GetChatMessagesUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.InsertChatMessageUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.RequestAssistantReplyUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.SendChatMessageUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.UpdateChatTitleUseCase
import java.util.UUID

internal class RequestAssistantReplyUseCaseImpl @Inject constructor(
    private val getChatMessagesUseCase: GetChatMessagesUseCase,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val insertChatMessageUseCase: InsertChatMessageUseCase,
    private val updateChatTitleUseCase: UpdateChatTitleUseCase,
    private val observeChatByIdUseCase: ObserveChatByIdUseCase,
    private val generateChatTitleUseCase: GenerateChatTitleUseCase,
) : RequestAssistantReplyUseCase {

    override suspend fun invoke(chatId: String): Result<Unit> {
        return runCatchingCancelable {
            // 1. Получаем историю сообщений для контекста GigaChat
            val history = getChatMessagesUseCase(chatId)
            val apiMessages = history.map { msg ->
                ChatCompletionMessage(
                    role = msg.role,
                    content = msg.text,
                    functionsStateId = msg.functionsStateId?.takeIf { msg.role == ASSISTANT },
                )
            }

            // 2. Делаем запрос к нейросети
            val assistantReply = sendChatMessageUseCase(apiMessages).getOrThrow()

            // 3. Сохраняем ответ ассистента в БД
            val isFirstAssistantReply = history.none { it.role == ASSISTANT }

            insertChatMessageUseCase(
                ChatMessageModel(
                    id = UUID.randomUUID().toString(),
                    chatId = chatId,
                    role = ASSISTANT,
                    text = assistantReply.content,
                    createdAtEpochMillis = System.currentTimeMillis(),
                    functionsStateId = assistantReply.functionsStateId,
                )
            )

            // 4. Если это первый ответ — генерируем и обновляем заголовок чата
            if (isFirstAssistantReply) {
                val currentChat = observeChatByIdUseCase(chatId).first()

                val newTitle = generateChatTitleUseCase(
                    assistantText = assistantReply.content,
                    currentTitle = currentChat?.title.orEmpty()
                )

                updateChatTitleUseCase(chatId, newTitle)
            }
        }
    }

    private companion object {
        private const val ASSISTANT = "assistant"
    }
}
