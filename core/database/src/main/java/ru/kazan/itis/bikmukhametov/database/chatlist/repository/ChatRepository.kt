package ru.kazan.itis.bikmukhametov.database.chatlist.repository

import kotlinx.coroutines.flow.Flow
import ru.kazan.itis.bikmukhametov.database.chatlist.entity.ChatEntity
import ru.kazan.itis.bikmukhametov.database.chatlist.entity.ChatMessageEntity

interface ChatRepository {

    suspend fun getChatsPage(offset: Int, limit: Int): List<ChatEntity>

    fun observeChats(): Flow<List<ChatEntity>>

    suspend fun getChatsPageByTitle(query: String): List<ChatEntity>

    suspend fun createChat(title: String = DEFAULT_NEW_CHAT_TITLE): String

    suspend fun getChatById(id: String): ChatEntity?

    fun observeMessages(chatId: String): Flow<List<ChatMessageEntity>>

    suspend fun getMessagesForChat(chatId: String): List<ChatMessageEntity>

    suspend fun insertMessage(message: ChatMessageEntity)

    suspend fun updateChatTitle(chatId: String, title: String)

    companion object {
        const val DEFAULT_NEW_CHAT_TITLE = "Новый чат"
    }
}
