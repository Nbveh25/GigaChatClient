package ru.kazan.itis.bikmukhametov.auth.data.datasource

interface AuthDataSource {
    val currentUser: Any?

    suspend fun awaitCurrentUserPresent(): Boolean

    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<Unit>

    suspend fun signInWithGoogle(idToken: String): Result<Unit>

    suspend fun registerWithEmailAndPassword(email: String, password: String): Result<Unit>

    fun signOut()
}
