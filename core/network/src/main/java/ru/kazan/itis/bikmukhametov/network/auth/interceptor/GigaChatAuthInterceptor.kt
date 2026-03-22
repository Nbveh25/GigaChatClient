package ru.kazan.itis.bikmukhametov.network.auth.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import ru.kazan.itis.bikmukhametov.network.BuildConfig
import java.util.UUID

class GigaChatAuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header("Authorization", "Basic ${BuildConfig.AUTH_KEY}")
            .header("RqUID", UUID.randomUUID().toString())
            .build()
        return chain.proceed(request)
    }
}
