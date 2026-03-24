package ru.kazan.itis.bikmukhametov.feature.chatlist.impl.presentation.screen

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.kazan.itis.bikmukhametov.common.util.viewmodel.BaseViewModel
import ru.kazan.itis.bikmukhametov.feature.chatlist.api.usecase.CreateChatUseCase
import ru.kazan.itis.bikmukhametov.feature.chatlist.api.usecase.LoadChatsBySearchUseCase
import ru.kazan.itis.bikmukhametov.feature.chatlist.api.usecase.LoadChatsUseCase

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val loadChatsUseCase: LoadChatsUseCase,
    private val loadChatsBySearchUseCase: LoadChatsBySearchUseCase,
    private val createChatUseCase: CreateChatUseCase,
) : BaseViewModel<ChatListUiState, ChatListIntent>(ChatListUiState()) {

    private val _effect = MutableSharedFlow<ChatListEffect>(extraBufferCapacity = 64)
    val effect: SharedFlow<ChatListEffect> = _effect.asSharedFlow()

    private var loadedItemsCount = 0

    init {
        loadInitialPage()
    }

    override fun onIntent(action: ChatListIntent) {
        when (action) {
            is ChatListIntent.SearchTextChanged -> {
                updateState { copy(searchFieldText = action.text) }
            }

            is ChatListIntent.SearchClicked -> onSearchClicked()

            is ChatListIntent.LoadNextPage -> loadNextPage()

            is ChatListIntent.CreateNewChatClicked -> createNewChat()

            is ChatListIntent.ChatItemClicked -> {
                emitEffect(ChatListEffect.NavigateToChat(action.chatId))
            }
        }
    }

    private fun loadInitialPage() {
        if (state.value.isChatListLoading) return

        viewModelScope.launch {
            updateState {
                copy(
                    isChatListLoading = true,
                    isNextPageLoading = false,
                    canLoadMore = true,
                )
            }

            loadedItemsCount = 0

            loadChatsUseCase(offset = 0, limit = PAGE_SIZE)
                .onSuccess { list ->
                    loadedItemsCount = list.size
                    updateState {
                        copy(
                            chats = list,
                            isChatListLoading = false,
                            isNextPageLoading = false,
                            canLoadMore = list.size == PAGE_SIZE,
                            isSearchActive = false,
                        )
                    }
                }
                .onFailure {
                    updateState {
                        copy(
                            chats = emptyList(),
                            isChatListLoading = false,
                            isNextPageLoading = false,
                            canLoadMore = false,
                            isSearchActive = false,
                        )
                    }
                }
        }
    }

    private fun loadNextPage() {
        val currentState = state.value

        if (currentState.isSearchActive || currentState.isChatListLoading || currentState.isNextPageLoading || !currentState.canLoadMore) {
            return
        }

        viewModelScope.launch {
            updateState { copy(isNextPageLoading = true) }

            loadChatsUseCase(offset = loadedItemsCount, limit = PAGE_SIZE)
                .onSuccess { list ->
                    if (list.isEmpty()) {
                        updateState { copy(isNextPageLoading = false, canLoadMore = false) }
                        return@onSuccess
                    }

                    loadedItemsCount += list.size
                    updateState {
                        copy(
                            chats = chats + list,
                            isNextPageLoading = false,
                            canLoadMore = list.size == PAGE_SIZE,
                        )
                    }
                }
                .onFailure {
                    updateState { copy(isNextPageLoading = false) }
                }
        }
    }

    private fun onSearchClicked() {
        val query = state.value.searchFieldText.trim()

        if (query.isEmpty()) {
            loadInitialPage()
            return
        }

        viewModelScope.launch {
            updateState { copy(isChatListLoading = true, isSearchActive = true, canLoadMore = false) }
            loadChatsBySearchUseCase(query)
                .onSuccess { list ->
                    updateState {
                        copy(
                            chats = list,
                            isChatListLoading = false,
                            isSearchActive = true,
                            isNextPageLoading = false,
                            canLoadMore = false,
                        )
                    }
                }
                .onFailure {
                    updateState {
                        copy(
                            chats = emptyList(),
                            isChatListLoading = false,
                            isSearchActive = true,
                            isNextPageLoading = false,
                            canLoadMore = false,
                        )
                    }
                }
        }
    }

    private fun createNewChat() {
        viewModelScope.launch {
            if (state.value.isCreatingChat) return@launch
            updateState { copy(isCreatingChat = true) }
            createChatUseCase()
                .onSuccess { chatId ->
                    loadInitialPage()
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

    private companion object {
        private const val PAGE_SIZE = 20
    }
}
