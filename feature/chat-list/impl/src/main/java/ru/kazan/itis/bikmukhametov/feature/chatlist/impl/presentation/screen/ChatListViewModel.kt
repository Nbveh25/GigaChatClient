package ru.kazan.itis.bikmukhametov.feature.chatlist.impl.presentation.screen

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.kazan.itis.bikmukhametov.common.util.viewmodel.BaseViewModel
import ru.kazan.itis.bikmukhametov.feature.chatlist.api.model.ChatModel
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

    private var loadGeneration = 0L

    init {
        reloadChats()
    }

    override fun onIntent(action: ChatListIntent) {
        when (action) {
            is ChatListIntent.SearchTextChanged -> {
                updateState { copy(searchFieldText = action.text) }
            }

            is ChatListIntent.SearchClicked -> onSearchClicked()

            is ChatListIntent.CreateNewChatClicked -> createNewChat()

            is ChatListIntent.ChatItemClicked -> viewModelScope.launch {
                _effect.emit(ChatListEffect.NavigateToChat(action.chatId))
            }
        }
    }

    private fun reloadChats() {
        val gen = ++loadGeneration
        viewModelScope.launch {
            updateState { copy(isChatListLoading = true) }
            val result = loadAllChats()
            if (gen != loadGeneration) return@launch
            result.fold(
                onSuccess = { list ->
                    updateState { copy(chats = list, isChatListLoading = false) }
                },
                onFailure = {
                    updateState { copy(chats = emptyList(), isChatListLoading = false) }
                },
            )
        }
    }

    private fun onSearchClicked() {
        val query = state.value.searchFieldText.trim()
        if (query.isEmpty()) {
            reloadChats()
            return
        }

        val gen = ++loadGeneration
        viewModelScope.launch {
            updateState { copy(isChatListLoading = true) }
            val result = loadChatsBySearchUseCase(query)
            if (gen != loadGeneration) return@launch
            result.fold(
                onSuccess = { list ->
                    updateState { copy(chats = list, isChatListLoading = false) }
                },
                onFailure = {
                    updateState { copy(chats = emptyList(), isChatListLoading = false) }
                },
            )
        }
    }

    private suspend fun loadAllChats(): Result<List<ChatModel>> {
        val out = mutableListOf<ChatModel>()
        var offset = 0
        while (true) {
            val page = loadChatsUseCase(offset, 20).getOrElse { return Result.failure(it) }
            out.addAll(page)
            if (page.size < 20) break
            offset += page.size
        }
        return Result.success(out)
    }

    private fun createNewChat() {
        viewModelScope.launch {
            if (state.value.isCreatingChat) return@launch
            updateState { copy(isCreatingChat = true) }
            createChatUseCase()
                .onSuccess { chatId ->
                    updateState { copy(isCreatingChat = false) }
                    reloadChats()
                    _effect.emit(ChatListEffect.NavigateToChat(chatId))
                }
                .onFailure {
                    updateState { copy(isCreatingChat = false) }
                }
        }
    }
}
