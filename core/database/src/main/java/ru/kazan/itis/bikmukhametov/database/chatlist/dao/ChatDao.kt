package ru.kazan.itis.bikmukhametov.database.chatlist.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.kazan.itis.bikmukhametov.database.chatlist.entity.ChatEntity

@Dao
interface ChatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ChatEntity)

    @Query("SELECT * FROM chats ORDER BY created_at_ms DESC LIMIT :limit OFFSET :offset")
    suspend fun getChatsPaged(limit: Int, offset: Int): List<ChatEntity>

    @Query("SELECT * FROM chats ORDER BY created_at_ms DESC")
    fun observeChats(): Flow<List<ChatEntity>>

    @Query(
        """
        SELECT c.* FROM chats AS c
        WHERE c.rowid IN (
            SELECT rowid FROM chats_fts WHERE chats_fts MATCH :matchQuery
        )
        ORDER BY c.created_at_ms DESC
        """,
    )
    suspend fun getChatsByFtsMatch(matchQuery: String): List<ChatEntity>

    @Query("SELECT * FROM chats WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ChatEntity?

    @Query("SELECT * FROM chats WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<ChatEntity?>

    @Query("UPDATE chats SET title = :title WHERE id = :id")
    suspend fun updateTitle(id: String, title: String)
}
