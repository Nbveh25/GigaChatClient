package ru.kazan.itis.bikmukhametov.network.auth.token

import com.liftric.kvault.KVault
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    private val kvault: KVault
) {

    fun saveToken(token: String, expiresAt: Long) {
        kvault.set(KEY_ACCESS_TOKEN, token)
        kvault.set(KEY_EXPIRES_AT, expiresAt)
    }

    fun getAccessToken(): String? = kvault.string(KEY_ACCESS_TOKEN)

    fun isTokenValid(): Boolean {
        val expiresAt = kvault.long(KEY_EXPIRES_AT) ?: 0L
        return System.currentTimeMillis() < (expiresAt - EXPIRES_IN)
    }

    fun clear() {
        kvault.clear()
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_EXPIRES_AT = "expires_at"
        private const val EXPIRES_IN = 60_000

    }
}
