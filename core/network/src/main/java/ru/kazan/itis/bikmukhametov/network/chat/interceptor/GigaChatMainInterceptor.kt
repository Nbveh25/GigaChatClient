package ru.kazan.itis.bikmukhametov.network.chat.interceptor

import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import ru.kazan.itis.bikmukhametov.network.auth.repository.TokenRepository
import timber.log.Timber
import java.util.UUID

@Singleton
class GigaChatMainInterceptor @Inject constructor(
    private val tokenRepository: TokenRepository
) : Interceptor {

    private val sessionId = UUID.randomUUID().toString()

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { tokenRepository.getValidToken() }

        if (token == null) {
            Timber.e("GigaChatMainInterceptor: токен не получен, запрос будет без Authorization")
        } else {
            Timber.d("GigaChatMainInterceptor: url=%s token=%s…", chain.request().url, token.take(20))
        }

        val response = chain.proceed(buildRequest(chain.request(), token))

        if (response.code == 401 || response.code == 403) {

            Timber.w("GigaChatMainInterceptor: получен %d, сбрасываем токен и повторяем", response.code)

            response.close()
            tokenRepository.invalidateToken()
            val freshToken = runBlocking { tokenRepository.getValidToken() }

            if (freshToken == null) {
                Timber.e("GigaChatMainInterceptor: не удалось получить новый токен при повторе")
                return chain.proceed(buildRequest(chain.request(), null))
            }

            Timber.d("GigaChatMainInterceptor: повтор с новым токеном %s…", freshToken.take(20))

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
}
