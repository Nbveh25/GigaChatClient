package ru.kazan.itis.bikmukhametov.feature.chatlist.impl.presentation.screen

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.kazan.itis.bikmukhametov.common.util.viewmodel.BaseViewModel
import ru.kazan.itis.bikmukhametov.feature.chatlist.api.model.ChatModel
import ru.kazan.itis.bikmukhametov.feature.chatlist.api.usecase.CreateChatUseCase
import ru.kazan.itis.bikmukhametov.feature.chatlist.api.usecase.LoadChatsBySearchUseCase
import ru.kazan.itis.bikmukhametov.feature.chatlist.api.usecase.ObserveChatsUseCase

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val observeChatsUseCase: ObserveChatsUseCase,
    private val loadChatsBySearchUseCase: LoadChatsBySearchUseCase,
    private val createChatUseCase: CreateChatUseCase,
) : BaseViewModel<ChatListUiState, ChatListIntent>(ChatListUiState()) {

    private val _effect = MutableSharedFlow<ChatListEffect>(extraBufferCapacity = 64)
    val effect: SharedFlow<ChatListEffect> = _effect.asSharedFlow()

    /**
     * Пустая строка — показываем полный список из БД (в т.ч. актуальные title после ответа GigaChat).
     * Иначе — результаты поиска по FTS.
     */
    private val appliedSearchQuery = MutableStateFlow("")

    init {
        observeChatList()
    }

    override fun onIntent(action: ChatListIntent) {
        when (action) {
            is ChatListIntent.SearchTextChanged -> {
                updateState { copy(searchFieldText = action.text) }
            }

            is ChatListIntent.SearchClicked -> onSearchClicked()

            is ChatListIntent.CreateNewChatClicked -> createNewChat()

            is ChatListIntent.ChatItemClicked -> {
                emitEffect(ChatListEffect.NavigateToChat(action.chatId))
            }
        }
    }

    private fun observeChatList() {
        appliedSearchQuery
            .flatMapLatest { query ->
                updateState { copy(isSearchActive = query.isNotEmpty()) }

                if (query.isEmpty()) {
                    observeChatsUseCase()
                } else {
                    flow {
                        val list = loadChatsBySearchUseCase(query).getOrElse { emptyList() }
                        emit(list)
                    }
                }
            }
            .onEach { list ->
                updateState {
                    copy(chats = list, isChatListLoading = false)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun onSearchClicked() {
        val query = state.value.searchFieldText.trim()

        // Если запрос пустой, сбрасываем поиск
        if (query.isEmpty()) {
            appliedSearchQuery.value = ""
            updateState { copy(isSearchActive = false) }
            return
        }

        viewModelScope.launch {
            updateState { copy(isChatListLoading = true, isSearchActive = true) }
            appliedSearchQuery.value = query
        }
    }

    private fun createNewChat() {
        viewModelScope.launch {
            if (state.value.isCreatingChat) return@launch
            updateState { copy(isCreatingChat = true) }
            createChatUseCase()
                .onSuccess { chatId ->
                    // При создании нового чата сбрасываем поиск
                    appliedSearchQuery.value = ""
                    updateState {
                        copy(
                            isCreatingChat = false,
                            isSearchActive = false,
                            searchFieldText = ""
                        )
                    }
                    _effect.emit(ChatListEffect.NavigateToChat(chatId))
                }
                .onFailure {
                    updateState { copy(isCreatingChat = false) }
                }
        }
    }

    private fun emitEffect(effect: ChatListEffect) {
        viewModelScope.launch { _effect.emit(effect) }
    }
}
