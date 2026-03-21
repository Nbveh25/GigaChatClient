package ru.kazan.itis.bikmukhametov.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.kazan.itis.bikmukhametov.database.chatlist.dao.ChatDao
import ru.kazan.itis.bikmukhametov.database.chatlist.entity.ChatEntity
import ru.kazan.itis.bikmukhametov.database.chatlist.entity.ChatFts

@Database(
    entities = [ChatEntity::class, ChatFts::class],
    version = 2,
    exportSchema = false,
)
abstract class GigaChatDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
}