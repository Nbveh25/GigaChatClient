package ru.kazan.itis.bikmukhametov.network.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ru.kazan.itis.bikmukhametov.network.chat.repository.GigaChatMessagesRepository
import ru.kazan.itis.bikmukhametov.network.chat.repository.GigaChatMessagesRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkBindsModule {

    @Binds
    @Singleton
    abstract fun bindGigaChatMessagesRepository(
        impl: GigaChatMessagesRepositoryImpl,
    ): GigaChatMessagesRepository
}
