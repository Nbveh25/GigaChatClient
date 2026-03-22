package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.GetChatByIdUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.GetChatMessagesUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.InsertChatMessageUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.ObserveChatMessagesUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.SendChatMessageUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.UpdateChatTitleUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.domain.usecase.GetChatByIdUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.domain.usecase.GetChatMessagesUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.domain.usecase.InsertChatMessageUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.domain.usecase.ObserveChatMessagesUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.domain.usecase.SendChatMessageUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.domain.usecase.UpdateChatTitleUseCaseImpl

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ChatDetailModule {

    @Binds
    abstract fun bindGetChatById(impl: GetChatByIdUseCaseImpl): GetChatByIdUseCase

    @Binds
    abstract fun bindObserveChatMessages(impl: ObserveChatMessagesUseCaseImpl): ObserveChatMessagesUseCase

    @Binds
    abstract fun bindGetChatMessages(impl: GetChatMessagesUseCaseImpl): GetChatMessagesUseCase

    @Binds
    abstract fun bindInsertChatMessage(impl: InsertChatMessageUseCaseImpl): InsertChatMessageUseCase

    @Binds
    abstract fun bindUpdateChatTitle(impl: UpdateChatTitleUseCaseImpl): UpdateChatTitleUseCase

    @Binds
    abstract fun bindSendChatMessage(impl: SendChatMessageUseCaseImpl): SendChatMessageUseCase
}
