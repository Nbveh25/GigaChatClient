package ru.kazan.itis.bikmukhametov.database.chatlist.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.kazan.itis.bikmukhametov.database.chatlist.entity.ChatMessageEntity

@Dao
interface ChatMessageDao {

    @Query(
        """
        SELECT * FROM chat_messages
        WHERE chat_id = :chatId
        ORDER BY created_at_ms ASC
        """,
    )
    fun observeByChatId(chatId: String): Flow<List<ChatMessageEntity>>

    @Query(
        """
        SELECT * FROM chat_messages
        WHERE chat_id = :chatId
        ORDER BY created_at_ms ASC
        """,
    )
    suspend fun getByChatId(chatId: String): List<ChatMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ChatMessageEntity)
}
