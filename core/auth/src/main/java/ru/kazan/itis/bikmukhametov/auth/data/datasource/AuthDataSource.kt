package ru.kazan.itis.bikmukhametov.auth.data.datasource

interface AuthDataSource {
    val currentUser: Any?

    /**
     * Ждёт готовности Firebase Auth (восстановление сессии с диска), затем можно безопасно читать [currentUser].
     * @return true, если после готовности SDK пользователь вошёл.
     */
    suspend fun awaitCurrentUserPresent(): Boolean

    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<Unit>

    suspend fun signInWithGoogle(idToken: String): Result<Unit>

    suspend fun registerWithEmailAndPassword(email: String, password: String): Result<Unit>

    fun signOut()
}
