package ru.kazan.itis.bikmukhametov.feature.profile.impl.di

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Named
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import ru.kazan.itis.bikmukhametov.api.repository.AppThemeRepository
import ru.kazan.itis.bikmukhametov.api.repository.ProfileRepository
import ru.kazan.itis.bikmukhametov.api.resource.ImageResourceProvider
import ru.kazan.itis.bikmukhametov.api.upload.AvatarUploader
import ru.kazan.itis.bikmukhametov.api.usecase.GetAppThemeUseCase
import ru.kazan.itis.bikmukhametov.api.usecase.GetTokensCountUseCase
import ru.kazan.itis.bikmukhametov.api.usecase.GetUserProfileUseCase
import ru.kazan.itis.bikmukhametov.api.usecase.SelectImageUseCase
import ru.kazan.itis.bikmukhametov.api.usecase.SetAppThemeUseCase
import ru.kazan.itis.bikmukhametov.api.usecase.SignOutUseCase
import ru.kazan.itis.bikmukhametov.api.usecase.UpdateUserNameUseCase
import ru.kazan.itis.bikmukhametov.api.usecase.UploadProfilePhotoUseCase
import ru.kazan.itis.bikmukhametov.feature.profile.impl.data.api.GigaChatTokensApi
import ru.kazan.itis.bikmukhametov.feature.profile.impl.data.interceptor.GigaChatProfileInterceptor
import ru.kazan.itis.bikmukhametov.feature.profile.impl.data.repository.AppThemeRepositoryImpl
import ru.kazan.itis.bikmukhametov.feature.profile.impl.data.repository.ProfileRepositoryImpl
import ru.kazan.itis.bikmukhametov.feature.profile.impl.data.resource.ImageResourceProviderImpl
import ru.kazan.itis.bikmukhametov.feature.profile.impl.data.upload.AvatarUploaderImpl
import ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase.GetAppThemeUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase.GetTokensCountUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase.GetUserProfileUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase.SetAppThemeUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase.SelectImageUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase.SignOutUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase.UpdateUserNameUseCaseImpl
import ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase.UploadProfilePhotoUseCaseImpl
import ru.kazan.itis.bikmukhametov.network.BuildConfig
import ru.kazan.itis.bikmukhametov.network.auth.token.GigaChatAuthenticator

@Module
@InstallIn(SingletonComponent::class)
internal object ProfileProvideModule {

    @Provides
    @Singleton
    fun provideContentResolver(
        @ApplicationContext context: Context,
    ): ContentResolver = context.contentResolver

    @Provides
    @Singleton
    fun provideProfileSharedPreferences(
        @ApplicationContext context: Context,
    ): SharedPreferences = context.getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    @Named("ProfileTokensOkHttp")
    fun provideProfileTokensOkHttp(
        profileInterceptor: GigaChatProfileInterceptor,
        authenticator: GigaChatAuthenticator,
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(profileInterceptor)
        .authenticator(authenticator)
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            },
        )
        .build()

    @Provides
    @Singleton
    fun provideGigaChatTokensApi(
        @Named("ProfileTokensOkHttp") okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory,
    ): GigaChatTokensApi {
        val baseUrl = BuildConfig.API_BASE_URL
        val url = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        return Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build()
            .create(GigaChatTokensApi::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ProfileModule {

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindAppThemeRepository(impl: AppThemeRepositoryImpl): AppThemeRepository

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
    abstract fun bindGetAppThemeUseCase(impl: GetAppThemeUseCaseImpl): GetAppThemeUseCase

    @Binds
    @Singleton
    abstract fun bindSetAppThemeUseCase(impl: SetAppThemeUseCaseImpl): SetAppThemeUseCase

    @Binds
    @Singleton
    abstract fun bindGetTokensCountUseCase(impl: GetTokensCountUseCaseImpl): GetTokensCountUseCase

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
