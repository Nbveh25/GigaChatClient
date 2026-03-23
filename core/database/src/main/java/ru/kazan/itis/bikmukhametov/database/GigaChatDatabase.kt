package ru.kazan.itis.bikmukhametov.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.kazan.itis.bikmukhametov.database.chatlist.dao.ChatDao
import ru.kazan.itis.bikmukhametov.database.chatlist.dao.ChatMessageDao
import ru.kazan.itis.bikmukhametov.database.chatlist.entity.ChatEntity
import ru.kazan.itis.bikmukhametov.database.chatlist.entity.ChatFts
import ru.kazan.itis.bikmukhametov.database.chatlist.entity.ChatMessageEntity

@Database(
    entities = [ChatEntity::class, ChatFts::class, ChatMessageEntity::class],
    version = 4,
    exportSchema = false,
)
abstract class GigaChatDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao

    abstract fun chatMessageDao(): ChatMessageDao
}
