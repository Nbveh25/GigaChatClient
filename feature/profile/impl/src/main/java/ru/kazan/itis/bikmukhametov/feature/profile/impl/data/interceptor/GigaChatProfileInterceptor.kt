package ru.kazan.itis.bikmukhametov.feature.profile.impl.data.interceptor

import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import ru.kazan.itis.bikmukhametov.network.auth.repository.TokenRepository
import java.util.UUID

@Singleton
internal class GigaChatProfileInterceptor @Inject constructor(
    private val tokenRepository: TokenRepository,
) : Interceptor {

    private val sessionId = UUID.randomUUID().toString()

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { tokenRepository.getValidToken() }
        val response = chain.proceed(buildRequest(chain.request(), token))

        if (response.code in HTTP_CODE_UNAUTHORIZED) {
            response.close()
            tokenRepository.invalidateToken()
            val freshToken = runBlocking { tokenRepository.getValidToken() }
            return chain.proceed(buildRequest(chain.request(), freshToken))
        }

        return response
    }

    private fun buildRequest(original: Request, token: String?): Request {
        return original.newBuilder()
            .apply { if (token != null) header("Authorization", "Bearer $token") }
            .header("X-Request-ID", UUID.randomUUID().toString())
            .header("X-Session-ID", sessionId)
            .build()
    }

    private companion object {
        private val HTTP_CODE_UNAUTHORIZED = 401..403
    }
}
