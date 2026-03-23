package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Named
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.data.api.GigaChatMainApi
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.data.interceptor.GigaChatMainInterceptor
import ru.kazan.itis.bikmukhametov.network.BuildConfig
import ru.kazan.itis.bikmukhametov.network.auth.token.GigaChatAuthenticator

@Module
@InstallIn(SingletonComponent::class)
object ChatMainNetworkModule {

    @Provides
    @Singleton
    @Named("MainOkHttp")
    fun provideMainOkHttp(
        mainInterceptor: GigaChatMainInterceptor,
        authenticator: GigaChatAuthenticator,
    ): OkHttpClient =
        OkHttpClient.Builder()
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
