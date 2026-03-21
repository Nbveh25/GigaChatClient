package ru.kazan.itis.bikmukhametov.feature.auth.api.repository

interface AuthRepository {
    val currentUser: Any?

    /** Ждёт восстановления сессии Firebase; `true`, если пользователь уже вошёл. */
    suspend fun awaitCurrentUserPresent(): Boolean

    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<Unit>

    suspend fun signInWithGoogle(idToken: String): Result<Unit>

    suspend fun registerWithEmailAndPassword(email: String, password: String): Result<Unit>

    fun signOut()
}