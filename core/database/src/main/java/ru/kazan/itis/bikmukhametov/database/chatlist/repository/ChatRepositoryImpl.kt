package ru.kazan.itis.bikmukhametov.database.chatlist.repository

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import ru.kazan.itis.bikmukhametov.database.chatlist.dao.ChatDao
import ru.kazan.itis.bikmukhametov.database.chatlist.dao.ChatMessageDao
import ru.kazan.itis.bikmukhametov.database.chatlist.entity.ChatEntity
import ru.kazan.itis.bikmukhametov.database.chatlist.entity.ChatMessageEntity

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao,
    private val chatMessageDao: ChatMessageDao,
) : ChatRepository {

    override suspend fun getChatsPage(offset: Int, limit: Int): List<ChatEntity> =
        chatDao.getChatsPaged(limit = limit, offset = offset)

    override fun observeChats(): Flow<List<ChatEntity>> = chatDao.observeChats()

    override suspend fun getChatsPageByTitle(query: String): List<ChatEntity> {
        val ftsMatch = buildFtsMatchQuery(query.trim())
        if (ftsMatch.isEmpty()) return emptyList()
        return chatDao.getChatsByFtsMatch(matchQuery = ftsMatch)
    }

    override suspend fun createChat(title: String): String {
        val id = UUID.randomUUID().toString()
        chatDao.insert(
            ChatEntity(
                id = id,
                title = title,
                createdAtEpochMillis = System.currentTimeMillis(),
            ),
        )
        return id
    }

    override suspend fun getChatById(id: String): ChatEntity? = chatDao.getById(id)

    override fun observeChatById(id: String): Flow<ChatEntity?> = chatDao.observeById(id)

    override fun observeMessages(chatId: String): Flow<List<ChatMessageEntity>> =
        chatMessageDao.observeByChatId(chatId)

    override suspend fun getMessagesForChat(chatId: String): List<ChatMessageEntity> =
        chatMessageDao.getByChatId(chatId)

    override suspend fun insertMessage(message: ChatMessageEntity) {
        chatMessageDao.insert(message)
    }

    override suspend fun updateChatTitle(chatId: String, title: String) {
        chatDao.updateTitle(id = chatId, title = title)
    }

    /**
     * Строит выражение для FTS4 MATCH: префиксный поиск по каждому слову (AND).
     * Кавычки в токенах экранируются по правилам SQLite FTS.
     */
    private fun buildFtsMatchQuery(trimmed: String): String {
        if (trimmed.isEmpty()) return ""
        return trimmed.split("\\s+".toRegex())
            .filter { it.isNotEmpty() }
            .joinToString(" AND ") { token ->
                val escaped = token.replace("\"", "\"\"")
                "\"$escaped\"*"
            }
    }
}
