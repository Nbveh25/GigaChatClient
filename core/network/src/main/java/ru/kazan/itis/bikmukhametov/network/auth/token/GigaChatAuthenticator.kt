package ru.kazan.itis.bikmukhametov.network.auth.token

import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import ru.kazan.itis.bikmukhametov.network.auth.repository.AuthRepository
import javax.inject.Inject

class GigaChatAuthenticator @Inject constructor(
    private val authRepository: AuthRepository
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.priorResponse != null) return null

        val newToken = runBlocking { authRepository.getValidToken() }
        return newToken?.let {
            response.request.newBuilder()
                .header("Authorization", "Bearer $it")
                .build()
        }
    }
}
