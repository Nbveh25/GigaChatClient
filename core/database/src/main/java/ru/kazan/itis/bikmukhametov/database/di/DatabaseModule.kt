package ru.kazan.itis.bikmukhametov.database.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ru.kazan.itis.bikmukhametov.database.GigaChatDatabase
import ru.kazan.itis.bikmukhametov.database.chatlist.dao.ChatDao

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GigaChatDatabase =
        Room.databaseBuilder(
            context,
            GigaChatDatabase::class.java,
            "gigachat.db",
        ).fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideChatDao(db: GigaChatDatabase): ChatDao = db.chatDao()

    @Provides
    fun provideChatMessageDao(db: GigaChatDatabase) = db.chatMessageDao()

}
