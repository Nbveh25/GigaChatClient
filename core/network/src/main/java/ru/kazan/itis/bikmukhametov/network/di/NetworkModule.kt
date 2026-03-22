package ru.kazan.itis.bikmukhametov.network.di

import android.content.Context
import com.liftric.kvault.KVault
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Named
import jakarta.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import ru.kazan.itis.bikmukhametov.network.BuildConfig
import ru.kazan.itis.bikmukhametov.network.auth.api.GigaChatAuthApi
import ru.kazan.itis.bikmukhametov.network.auth.interceptor.GigaChatAuthInterceptor
import ru.kazan.itis.bikmukhametov.network.auth.token.GigaChatAuthenticator
import ru.kazan.itis.bikmukhametov.network.chat.api.GigaChatMainApi
import ru.kazan.itis.bikmukhametov.network.chat.interceptor.GigaChatMainInterceptor

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true // Чтобы не падало при новых полях в API
        coerceInputValues = true // На случай несовпадения типов null/default
    }

    @Provides
    @Singleton
    fun provideConverterFactory(json: Json): Converter.Factory {
        val contentType = "application/json".toMediaType()
        return json.asConverterFactory(contentType)
    }

    // окхттп для получения токена
    @Provides
    @Singleton
    @Named("AuthOkHttp")
    fun provideAuthOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            // Добавляем наш интерцептор авторизации
            .addInterceptor(GigaChatAuthInterceptor())
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    // Ретрофит для получения токена
    @Provides
    @Singleton
    fun provideGigaChatAuthApi(
        @Named("AuthOkHttp") okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory
    ): GigaChatAuthApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.AUTH_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build()
            .create(GigaChatAuthApi::class.java)
    }

    // OkHttp для чата (с авторизацией через Authenticator)
    @Provides
    @Singleton
    @Named("MainOkHttp")
    fun provideChatOkHttpClient(
        mainInterceptor: GigaChatMainInterceptor,
        authenticator: GigaChatAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(mainInterceptor)
            .authenticator(authenticator)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    // Ретрофит для чата GigaChat
    @Provides
    @Singleton
    fun provideGigaChatMainApi(
        @Named("MainOkHttp") okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory,
    ): GigaChatMainApi {
        val baseUrl = BuildConfig.API_BASE_URL
        val url = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        return Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build()
            .create(GigaChatMainApi::class.java)
    }

    @Provides
    @Singleton
    fun provideKVault(@ApplicationContext context: Context): KVault {
        return KVault(context, "gigachat_secure_prefs")
    }

}
