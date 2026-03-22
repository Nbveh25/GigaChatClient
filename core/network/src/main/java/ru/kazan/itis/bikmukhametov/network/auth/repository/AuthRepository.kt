package ru.kazan.itis.bikmukhametov.network.auth.repository

import ru.kazan.itis.bikmukhametov.network.auth.api.GigaChatAuthApi
import ru.kazan.itis.bikmukhametov.network.auth.token.TokenManager
import ru.kazan.itis.bikmukhametov.common.util.error.runCatchingCancelable
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: GigaChatAuthApi,
    private val tokenManager: TokenManager
) {

    suspend fun getValidToken(): String? {
        val cached = tokenManager.getAccessToken()?.takeIf { tokenManager.isTokenValid() }
        if (cached != null) return cached
        return fetchAndSaveNewToken()
    }

    private suspend fun fetchAndSaveNewToken(): String? = runCatchingCancelable {
        authApi.getAccessToken()
    }.onSuccess { response ->
        tokenManager.saveToken(response.accessToken, response.expiresAt)
    }.onFailure { e ->
        Timber.e(e, "GigaChat token fetch failed: %s", e.message)
    }.getOrNull()?.accessToken
}
