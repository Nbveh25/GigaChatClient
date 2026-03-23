package ru.kazan.itis.bikmukhametov.feature.profile.impl.di

import android.content.ContentResolver
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ru.kazan.itis.bikmukhametov.api.repository.ProfileRepository
import ru.kazan.itis.bikmukhametov.api.resource.ImageResourceProvider
import ru.kazan.itis.bikmukhametov.api.upload.AvatarUploader
import ru.kazan.itis.bikmukhametov.api.usecase.GetUserProfileUseCase
import ru.kazan.itis.bikmukhametov.api.usecase.SelectImageUseCase
import ru.kazan.itis.bikmukhametov.api.usecase.SignOutUseCase
import ru.kazan.itis.bikmukhametov.api.usecase.UpdateUserNameUseCase
import ru.kazan.itis.bikmukhametov.api.usecase.UploadProfilePhotoUseCase
import ru.kazan.itis.bikmukhametov.feature.profile.impl.data.repository.ProfileRepositoryImpl
import ru.kazan.itis.bikmukhametov.feature.profile.impl.data.resource.ImageResourceProviderImpl
import ru.kazan.itis.bikmukhametov.feature.profile.impl.data.upload.AvatarUploaderImpl
import ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase.GetUserProfileUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase.SelectImageUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase.SignOutUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase.UpdateUserNameUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase.UploadProfilePhotoUseCaseImpl

@Module
@InstallIn(SingletonComponent::class)
internal object ProfileProvideModule {

    @Provides
    @Singleton
    fun provideContentResolver(
        @ApplicationContext context: Context,
    ): ContentResolver = context.contentResolver
}

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ProfileModule {

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindImageResourceProvider(impl: ImageResourceProviderImpl): ImageResourceProvider

    @Binds
    @Singleton
    abstract fun bindAvatarUploader(impl: AvatarUploaderImpl): AvatarUploader

    @Binds
    @Singleton
    abstract fun bindGetUserProfileUseCase(impl: GetUserProfileUseCaseImpl): GetUserProfileUseCase

    @Binds
    @Singleton
    abstract fun bindSelectImageUseCase(impl: SelectImageUseCaseImpl): SelectImageUseCase

    @Binds
    @Singleton
    abstract fun bindUpdateUserNameUseCase(impl: UpdateUserNameUseCaseImpl): UpdateUserNameUseCase

    @Binds
    @Singleton
    abstract fun bindUploadProfilePhotoUseCase(impl: UploadProfilePhotoUseCaseImpl): UploadProfilePhotoUseCase

    @Binds
    @Singleton
    abstract fun bindSignOutUseCase(impl: SignOutUseCaseImpl): SignOutUseCase
}
