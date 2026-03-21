package ru.kazan.itis.bikmukhametov.database.chatlist.repository

import ru.kazan.itis.bikmukhametov.database.chatlist.entity.ChatEntity

interface ChatRepository {

    suspend fun getChatsPage(offset: Int, limit: Int): List<ChatEntity>

    suspend fun getChatsPageByTitle(query: String): List<ChatEntity>

    suspend fun createChat(title: String = DEFAULT_NEW_CHAT_TITLE): String

    companion object {
        const val DEFAULT_NEW_CHAT_TITLE = "Новый чат"
    }
}