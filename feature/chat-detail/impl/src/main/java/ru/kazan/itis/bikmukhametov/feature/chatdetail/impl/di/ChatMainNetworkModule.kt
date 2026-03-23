package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Named
import javax.inject.Singleton
import java.util.concurrent.TimeUnit
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.data.api.GigaChatMainApi
import ru.kazan.itis.bikmukhametov.network.BuildConfig
import ru.kazan.itis.bikmukhametov.network.auth.token.GigaChatAuthenticator

@Module
@InstallIn(SingletonComponent::class)
object ChatMainNetworkModule {

    @Provides
    @Singleton
    @Named("MainOkHttp")
    fun provideMainOkHttp(
        @Named("GigaChatApiInterceptor") mainInterceptor: Interceptor,
        authenticator: GigaChatAuthenticator,
    ): OkHttpClient =
        OkHttpClient.Builder()
            // Дефолт OkHttp — 10 с на connect/read; completions и text2image у GigaChat часто дольше.
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(120, TimeUnit.SECONDS)
            .addInterceptor(mainInterceptor)
            .authenticator(authenticator)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                },
            )
            .build()

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
}
