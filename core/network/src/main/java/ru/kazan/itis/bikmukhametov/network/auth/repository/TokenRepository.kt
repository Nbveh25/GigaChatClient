package ru.kazan.itis.bikmukhametov.network.auth.repository

import jakarta.inject.Inject
import jakarta.inject.Singleton
import ru.kazan.itis.bikmukhametov.common.util.error.runCatchingCancelable
import ru.kazan.itis.bikmukhametov.network.auth.api.GigaChatAuthApi
import ru.kazan.itis.bikmukhametov.network.auth.token.TokenManager
import timber.log.Timber

@Singleton
class TokenRepository @Inject constructor(
    private val authApi: GigaChatAuthApi,
    private val tokenManager: TokenManager
) {

    suspend fun getValidToken(): String? {
        val cached = tokenManager.getAccessToken()?.takeIf { tokenManager.isTokenValid() }
        if (cached != null) {
            Timber.d("GigaChat token (from cache): %s", cached)
            return cached
        }
        val newToken = fetchAndSaveNewToken()
        if (newToken != null) {
            Timber.d("GigaChat token (fresh): %s", newToken)
        } else {
            Timber.w("GigaChat token: не удалось получить токен")
        }
        return newToken
    }

    fun invalidateToken() {
        Timber.d("GigaChat token: инвалидация кэша токена")
        tokenManager.clear()
    }

    private suspend fun fetchAndSaveNewToken(): String? {
        Timber.d("GigaChat token: запрос нового токена...")
        val result = runCatchingCancelable {
            authApi.getAccessToken()
        }
        return result
            .onSuccess { response ->
                tokenManager.saveToken(response.accessToken, response.expiresAt)
            }
            .onFailure { e ->
                Timber.e(e, "GigaChat token fetch failed: %s", e.message)
            }
            .getOrNull()?.accessToken
    }

}
