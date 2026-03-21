package ru.kazan.itis.bikmukhametov.feature.chatlist.impl.presentation.screen

sealed interface ChatListEffect {
    data class NavigateToChat(val chatId: String) : ChatListEffect
}
