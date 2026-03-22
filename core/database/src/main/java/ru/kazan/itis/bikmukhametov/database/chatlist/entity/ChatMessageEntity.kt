package ru.kazan.itis.bikmukhametov.database.chatlist.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chat_messages",
    foreignKeys = [
        ForeignKey(
            entity = ChatEntity::class,
            parentColumns = ["id"],
            childColumns = ["chat_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["chat_id"])],
)
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "chat_id") val chatId: String,
    @ColumnInfo(name = "role") val role: String,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "created_at_ms") val createdAtEpochMillis: Long,
)
