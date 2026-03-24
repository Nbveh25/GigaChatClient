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

private const val CONNECT_TIMEOUT = 60L
private const val READ_TIMEOUT = 5L
private const val WRITE_TIMEOUT = 120L

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
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.MINUTES)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
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
