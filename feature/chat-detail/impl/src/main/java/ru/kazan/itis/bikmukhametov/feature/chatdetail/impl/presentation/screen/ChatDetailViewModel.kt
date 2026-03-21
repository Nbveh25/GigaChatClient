package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.presentation.screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.kazan.itis.bikmukhametov.common.util.viewmodel.BaseViewModel
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.presentation.screen.ChatDetailMocks

private const val CHAT_ID_ARG = "chatId"

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<ChatDetailUiState, ChatDetailIntent>(
    ChatDetailMocks.initialState(
        chatTitle = savedStateHandle.get<String>(CHAT_ID_ARG)
            ?.let { ChatDetailMocks.defaultChatTitle(it) }
            ?: "",
    ),
) {

    private val _effect = MutableSharedFlow<ChatDetailEffect>(extraBufferCapacity = 64)
    val effect: SharedFlow<ChatDetailEffect> = _effect.asSharedFlow()

    private var failNextGenerationOnce = true

    init {
        val title = savedStateHandle.get<String>(CHAT_ID_ARG)
            ?.let { ChatDetailMocks.defaultChatTitle(it) }
            ?: ""
        if (state.value.chatTitle.isEmpty() && title.isNotEmpty()) {
            updateState { copy(chatTitle = title) }
        }
    }

    override fun onIntent(action: ChatDetailIntent) {
        when (action) {
            is ChatDetailIntent.InputTextChanged -> updateState {
                copy(inputText = action.value)
            }

            ChatDetailIntent.SendClicked -> send()

            ChatDetailIntent.ClearInputClicked -> updateState {
                copy(inputText = "")
            }

            ChatDetailIntent.RetryClicked -> retry()

            is ChatDetailIntent.ShareAssistantText -> {
                shareText(action)
            }
        }
    }

    private fun send() {
        val current = state.value
        val trimmed = current.inputText.trim()
        if (trimmed.isEmpty() || current.isGenerating) return

        updateState {
            copy(
                messages = messages + ChatMessageUi(role = ChatMessageRole.User, text = trimmed),
                inputText = "",
                generationError = false,
                pendingRetryText = null,
            )
        }
        runMockGeneration(trimmed, isRetry = false)
    }

    private fun retry() {
        val pending = state.value.pendingRetryText ?: return
        runMockGeneration(pending, isRetry = true)
    }

    private fun runMockGeneration(userText: String, isRetry: Boolean) {
        if (userText.isBlank() || state.value.isGenerating) return

        viewModelScope.launch {
            updateState {
                copy(
                    isGenerating = true,
                    generationError = false,
                    pendingRetryText = null,
                )
            }
            delay(1200)
            if (failNextGenerationOnce && !isRetry) {
                failNextGenerationOnce = false
                updateState {
                    copy(
                        isGenerating = false,
                        generationError = true,
                        pendingRetryText = userText,
                    )
                }
                return@launch
            }
            updateState {
                copy(
                    isGenerating = false,
                    generationError = false,
                    pendingRetryText = null,
                    messages = messages + ChatMessageUi(
                        role = ChatMessageRole.Assistant,
                        text = ChatDetailMocks.mockAssistantReply(userText),
                    ),
                )
            }
        }
    }

    private fun shareText(action: ChatDetailIntent.ShareAssistantText) {
        viewModelScope.launch {
            _effect.emit(ChatDetailEffect.ShareText(action.text))
        }
    }
}
