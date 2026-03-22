package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.presentation.screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.kazan.itis.bikmukhametov.common.util.viewmodel.BaseViewModel
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.ChatDetailConstants
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.model.ChatMessageModel
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.GetChatByIdUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.GetChatMessagesUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.InsertChatMessageUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.ObserveChatMessagesUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.SendChatMessageUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.UpdateChatTitleUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.presentation.item.ChatMessageItem
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.presentation.item.toItem
import timber.log.Timber

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val getChatByIdUseCase: GetChatByIdUseCase,
    private val observeChatMessagesUseCase: ObserveChatMessagesUseCase,
    private val getChatMessagesUseCase: GetChatMessagesUseCase,
    private val insertChatMessageUseCase: InsertChatMessageUseCase,
    private val updateChatTitleUseCase: UpdateChatTitleUseCase,
) : BaseViewModel<ChatDetailUiState, ChatDetailIntent>(ChatDetailUiState()) {

    private val chatId: String = savedStateHandle.get<String>(CHAT_ID_ARG) ?: ""

    private val _effect = MutableSharedFlow<ChatDetailEffect>(extraBufferCapacity = 64)
    val effect: SharedFlow<ChatDetailEffect> = _effect.asSharedFlow()

    init {
        val chatIdArg = savedStateHandle.get<String>(CHAT_ID_ARG)
        val title = chatIdArg?.let { ChatDetailMocks.defaultChatTitle(it) } ?: ""

        Timber.d("ChatDetail init: chatId=$chatIdArg, title=$title")

        if (state.value.chatTitle.isEmpty() && title.isNotEmpty()) {
            updateState { copy(chatTitle = title) }
        }

        if (chatId.isNotBlank()) {
            viewModelScope.launch {
                val chat = getChatByIdUseCase(chatId)
                if (chat != null) {
                    updateState {
                        copy(chatTitle = chat.title)
                    }
                }
            }

            observeChatMessagesUseCase(chatId)
                .onEach { messages ->
                    updateState {
                        copy(messages = messages.map {
                            it.toItem()
                        })
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    override fun onIntent(action: ChatDetailIntent) {
        when (action) {
            is ChatDetailIntent.InputTextChanged -> updateState {
                copy(inputText = action.value)
            }

            is ChatDetailIntent.SendClicked -> send()

            is ChatDetailIntent.ClearInputClicked -> updateState {
                copy(inputText = "")
            }

            is ChatDetailIntent.RetryClicked -> retry()

            is ChatDetailIntent.ShareAssistantText -> {
                shareText(action)
            }
        }
    }

    private fun send() {
        val current = state.value
        val trimmed = current.inputText.trim()
        if (trimmed.isEmpty() || current.isGenerating) return
        if (chatId.isBlank()) {
            Timber.w("ChatDetail send: empty chatId")
            return
        }

        viewModelScope.launch {
            updateState {
                copy(
                    inputText = "",
                    generationError = false,
                    generationErrorMessage = null,
                    pendingRetryText = null,
                )
            }
            insertChatMessageUseCase(
                ChatMessageModel(
                    id = UUID.randomUUID().toString(),
                    chatId = chatId,
                    role = USER_ROLE,
                    text = trimmed,
                    createdAtEpochMillis = System.currentTimeMillis(),
                ),
            )
            sendToGigaChat()
        }
    }

    private fun retry() {
        if (state.value.pendingRetryText == null) return
        sendToGigaChat()
    }

    private fun sendToGigaChat() {
        if (state.value.isGenerating) return
        if (chatId.isBlank()) return

        viewModelScope.launch {
            updateState {
                copy(
                    isGenerating = true,
                    generationError = false,
                    generationErrorMessage = null,
                    pendingRetryText = null,
                )
            }
            val dbMessages = getChatMessagesUseCase(chatId)
            val lastUserMessage = dbMessages.lastOrNull { it.role == USER_ROLE }?.text
            if (lastUserMessage.isNullOrBlank()) {
                updateState { copy(isGenerating = false) }
                return@launch
            }
            val apiMessages = dbMessages.map { it.role to it.text }

            Timber.d("ChatDetail sendToGigaChat: messagesCount=%d", apiMessages.size)

            sendChatMessageUseCase(apiMessages)
                .onSuccess { assistantText ->

                    Timber.d("ChatDetail success: responseLength=%d", assistantText.length)

                    val isFirstAssistantReply = dbMessages.none { it.role == ASSISTANT_ROLE }
                    insertChatMessageUseCase(
                        ChatMessageModel(
                            id = UUID.randomUUID().toString(),
                            chatId = chatId,
                            role = ASSISTANT_ROLE,
                            text = assistantText,
                            createdAtEpochMillis = System.currentTimeMillis(),
                        ),
                    )
                    val newTitle = if (isFirstAssistantReply) {
                        val t = chatTitleFromAssistantReply(assistantText)
                        updateChatTitleUseCase(chatId, t)
                        t
                    } else {
                        null
                    }
                    updateState {
                        copy(
                            isGenerating = false,
                            generationError = false,
                            generationErrorMessage = null,
                            pendingRetryText = null,
                            chatTitle = newTitle ?: chatTitle,
                        )
                    }
                }
                .onFailure {
                    val errorMsg = it.message ?: it.javaClass.simpleName

                    Timber.e(it, "ChatDetail failure: %s", errorMsg)

                    updateState {
                        copy(
                            isGenerating = false,
                            generationError = true,
                            generationErrorMessage = errorMsg,
                            pendingRetryText = lastUserMessage,
                        )
                    }
                }
        }
    }

    private fun shareText(action: ChatDetailIntent.ShareAssistantText) {
        viewModelScope.launch {
            _effect.emit(ChatDetailEffect.ShareText(action.text))
        }
    }

    private fun chatTitleFromAssistantReply(assistantText: String): String {
        val line = assistantText.replace('\n', ' ').trim()
        if (line.isEmpty()) {
            return state.value.chatTitle.ifBlank { ChatDetailConstants.DEFAULT_CHAT_TITLE }
        }
        val words = line.split(Regex("\\s+")).filter { it.isNotEmpty() }.take(TITLE_MAX_WORDS)
        var title = words.joinToString(" ")
        if (title.length > TITLE_MAX_CHARS) {
            title =
                title.take(TITLE_MAX_CHARS).trimEnd { it.isWhitespace() || it == ',' || it == '.' }
            if (title.isEmpty()) title = line.take(TITLE_MAX_CHARS)
        }
        return title
    }

    private companion object {
        private const val CHAT_ID_ARG = "chatId"
        private const val TITLE_MAX_WORDS = 6
        private const val TITLE_MAX_CHARS = 20
        private const val ASSISTANT_ROLE = "assistant"
        private const val USER_ROLE = "user"
    }
}
