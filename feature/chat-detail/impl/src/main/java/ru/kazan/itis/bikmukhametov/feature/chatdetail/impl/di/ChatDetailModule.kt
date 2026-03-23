package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.repository.GigaChatMessagesRepository
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.DownloadGeneratedImageUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.GenerateChatTitleUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.ObserveChatByIdUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.GetChatMessagesUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.InsertChatMessageUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.ObserveChatMessagesUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.RequestAssistantReplyUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.SendChatMessageUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.UpdateChatTitleUseCase
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.data.repository.GigaChatMessagesRepositoryImpl
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.domain.usecase.GenerateChatTitleUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.domain.usecase.DownloadGeneratedImageUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.domain.usecase.ObserveChatByIdUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.domain.usecase.GetChatMessagesUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.domain.usecase.InsertChatMessageUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.domain.usecase.ObserveChatMessagesUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.domain.usecase.RequestAssistantReplyUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.domain.usecase.SendChatMessageUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.domain.usecase.UpdateChatTitleUseCaseImpl

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ChatDetailModule {

    @Binds
    abstract fun bindGigaChatMessagesRepository(impl: GigaChatMessagesRepositoryImpl): GigaChatMessagesRepository

    @Binds
    abstract fun bindRequestAssistantReplyUseCase(impl: RequestAssistantReplyUseCaseImpl): RequestAssistantReplyUseCase

    @Binds
    abstract fun bindObserveChatByIdUseCase(impl: ObserveChatByIdUseCaseImpl): ObserveChatByIdUseCase


    @Binds
    abstract fun bindObserveChatMessagesUseCase(impl: ObserveChatMessagesUseCaseImpl): ObserveChatMessagesUseCase

    @Binds
    abstract fun bindGetChatMessagesUseCase(impl: GetChatMessagesUseCaseImpl): GetChatMessagesUseCase

    @Binds
    abstract fun bindInsertChatMessageUseCase(impl: InsertChatMessageUseCaseImpl): InsertChatMessageUseCase

    @Binds
    abstract fun bindUpdateChatTitleUseCase(impl: UpdateChatTitleUseCaseImpl): UpdateChatTitleUseCase

    @Binds
    abstract fun bindSendChatMessageUseCase(impl: SendChatMessageUseCaseImpl): SendChatMessageUseCase

    @Binds
    abstract fun bindGenerateChatTitleUseCase(impl: GenerateChatTitleUseCaseImpl): GenerateChatTitleUseCase

    @Binds
    abstract fun bindDownloadGeneratedImageUseCase(impl: DownloadGeneratedImageUseCaseImpl): DownloadGeneratedImageUseCase
}
