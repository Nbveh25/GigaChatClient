package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.presentation.screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.kazan.itis.bikmukhametov.common.util.viewmodel.BaseViewModel
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.model.ChatMessageModel
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.ObserveChatByIdUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.InsertChatMessageUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.ObserveChatMessagesUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.RequestAssistantReplyUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.presentation.item.toItem

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observeChatByIdUseCase: ObserveChatByIdUseCase,
    private val insertChatMessageUseCase: InsertChatMessageUseCase,
    private val observeChatMessagesUseCase: ObserveChatMessagesUseCase,
    private val requestAssistantReplyUseCase: RequestAssistantReplyUseCase,
) : BaseViewModel<ChatDetailUiState, ChatDetailIntent>(ChatDetailUiState()) {

    private val chatId: String = savedStateHandle.get<String>(CHAT_ID_ARG) ?: ""

    private val _effect = MutableSharedFlow<ChatDetailEffect>(extraBufferCapacity = 64)
    val effect: SharedFlow<ChatDetailEffect> = _effect.asSharedFlow()

    init {
        observeChatData()
    }

    override fun onIntent(action: ChatDetailIntent) {
        when (action) {
            is ChatDetailIntent.InputTextChanged -> updateState { copy(inputText = action.value) }
            is ChatDetailIntent.SendClicked -> send()
            is ChatDetailIntent.ClearInputClicked -> updateState {
                copy(inputText = "")
            }
            is ChatDetailIntent.RetryClicked -> {
                sendToGigaChat(state.value.pendingRetryText)
            }
            is ChatDetailIntent.ShareAssistantText -> {
                emitEffect(ChatDetailEffect.ShareText(action.text))
            }
        }
    }

    private fun observeChatData() {
        if (chatId.isBlank()) return

        // заголовок
        observeChatByIdUseCase(chatId)
            .onEach { chat -> updateState { copy(chatTitle = chat?.title ?: chatTitle) } }
            .launchIn(viewModelScope)

        // сообщения
        observeChatMessagesUseCase(chatId)
            .map { messages -> messages.map { it.toItem() } }
            .flowOn(Dispatchers.Default)
            .onEach { items -> updateState { copy(messages = items) } }
            .launchIn(viewModelScope)
    }

    private fun send() {
        val text = state.value.inputText.trim()
        if (text.isEmpty() || state.value.isGenerating || chatId.isBlank()) return

        viewModelScope.launch {
            updateState { copy(inputText = "", generationError = false, pendingRetryText = null) }

            val userMsg = ChatMessageModel(
                id = UUID.randomUUID().toString(),
                chatId = chatId,
                role = USER_ROLE,
                text = text,
                createdAtEpochMillis = System.currentTimeMillis(),
            )

            insertChatMessageUseCase(userMsg)

            sendToGigaChat(text)
        }
    }

    private fun sendToGigaChat(retryText: String?) {
        if (chatId.isBlank()) return

        viewModelScope.launch {
            updateState { copy(isGenerating = true, generationError = false) }

            requestAssistantReplyUseCase(chatId)
                .onSuccess { updateState { copy(isGenerating = false) } }
                .onFailure { e ->
                    updateState {
                        copy(
                            isGenerating = false,
                            generationError = true,
                            generationErrorMessage = e.message,
                            pendingRetryText = retryText
                        )
                    }
                }
        }
    }

    private fun emitEffect(effect: ChatDetailEffect) {
        viewModelScope.launch { _effect.emit(effect) }
    }

    private companion object {
        private const val CHAT_ID_ARG = "chatId"
        private const val USER_ROLE = "user"
    }
}
