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

    private val currentOffset: Int get() = state.value.chats.size

    init {
        loadPage(isInitial = true)
    }

    override fun onIntent(action: ChatListIntent) {
        when (action) {
            is ChatListIntent.SearchTextChanged -> updateState { copy(searchFieldText = action.text) }
            is ChatListIntent.SearchClicked -> onSearchClicked()
            is ChatListIntent.LoadNextPage -> loadPage(isInitial = false)
            is ChatListIntent.CreateNewChatClicked -> createNewChat()
            is ChatListIntent.ChatItemClicked -> emitEffect(ChatListEffect.NavigateToChat(action.chatId))
        }
    }

    private fun loadPage(isInitial: Boolean) {
        val currentState = state.value

        if (isInitial && currentState.isChatListLoading) return
        if (!isInitial && (currentState.isNextPageLoading ||
                    !currentState.canLoadMore || currentState.isSearchActive)) return

        viewModelScope.launch {
            if (isInitial) {
                updateState { copy(isChatListLoading = true, canLoadMore = true) }
            } else {
                updateState { copy(isNextPageLoading = true) }
            }

            val offset = if (isInitial) 0 else currentOffset

            loadChatsUseCase(offset = offset, limit = PAGE_SIZE)
                .onSuccess { newList ->
                    updateState {
                        copy(
                            chats = if (isInitial) newList else chats + newList,
                            isChatListLoading = false,
                            isNextPageLoading = false,
                            canLoadMore = newList.size == PAGE_SIZE,
                            isSearchActive = false
                        )
                    }
                }
                .onFailure {
                    updateState {
                        copy(
                            isChatListLoading = false,
                            isNextPageLoading = false,
                            canLoadMore = false
                        )
                    }
                }
        }
    }

    private fun onSearchClicked() {
        val query = state.value.searchFieldText.trim()

        if (query.isEmpty()) {
            loadPage(isInitial = true)
            return
        }

        viewModelScope.launch {
            updateState { copy(isChatListLoading = true, isSearchActive = true, canLoadMore = false) }

            loadChatsBySearchUseCase(query)
                .onSuccess { list ->
                    updateState { copy(chats = list, isChatListLoading = false) }
                }
                .onFailure {
                    updateState { copy(chats = emptyList(), isChatListLoading = false) }
                }
        }
    }

    private fun createNewChat() {
        if (state.value.isCreatingChat) return

        viewModelScope.launch {
            updateState { copy(isCreatingChat = true) }

            createChatUseCase()
                .onSuccess { chatId ->
                    updateState { copy(isCreatingChat = false, searchFieldText = "") }
                    loadPage(isInitial = true)
                    emitEffect(ChatListEffect.NavigateToChat(chatId))
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
