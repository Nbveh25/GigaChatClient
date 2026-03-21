package ru.kazan.itis.bikmukhametov.feature.chatlist.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.kazan.itis.bikmukhametov.feature.chatlist.api.usecase.CreateChatUseCase
import ru.kazan.itis.bikmukhametov.feature.chatlist.api.usecase.LoadChatsBySearchUseCase
import ru.kazan.itis.bikmukhametov.feature.chatlist.api.usecase.LoadChatsUseCase
import ru.kazan.itis.bikmukhametov.feature.chatlist.impl.domain.usecase.CreateChatUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.chatlist.impl.domain.usecase.LoadChatsBySearchUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.chatlist.impl.domain.usecase.LoadChatsUseCaseImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class ChatListUseCaseModule {

    @Binds
    abstract fun bindLoadPagedChats(impl: LoadChatsUseCaseImpl): LoadChatsUseCase

    @Binds
    abstract fun bindLoadPagedChatsBySearch(impl: LoadChatsBySearchUseCaseImpl): LoadChatsBySearchUseCase

    @Binds
    abstract fun bindCreateChat(impl: CreateChatUseCaseImpl): CreateChatUseCase
}
