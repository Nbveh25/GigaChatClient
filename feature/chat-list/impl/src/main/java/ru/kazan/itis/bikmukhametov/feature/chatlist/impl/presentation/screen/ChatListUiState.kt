package ru.kazan.itis.bikmukhametov.feature.chatlist.impl.presentation.screen

import ru.kazan.itis.bikmukhametov.feature.chatlist.api.model.ChatModel

data class ChatListUiState(
    val searchFieldText: String = "",
    val isCreatingChat: Boolean = false,
    val chats: List<ChatModel> = emptyList(),
    val isChatListLoading: Boolean = false,
    val isNextPageLoading: Boolean = false,
    val canLoadMore: Boolean = true,
    val isSearchActive: Boolean = false,
)
