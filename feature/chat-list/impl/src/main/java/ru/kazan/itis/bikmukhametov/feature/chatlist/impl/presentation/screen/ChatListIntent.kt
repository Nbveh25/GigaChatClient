package ru.kazan.itis.bikmukhametov.feature.chatlist.impl.presentation.screen

sealed interface ChatListIntent {
    data class SearchTextChanged(val text: String) : ChatListIntent
    data object SearchClicked : ChatListIntent
    data object LoadNextPage : ChatListIntent
    data object CreateNewChatClicked : ChatListIntent
    data class ChatItemClicked(val chatId: String) : ChatListIntent
}
