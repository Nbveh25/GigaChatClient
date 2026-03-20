package ru.kazan.itis.bikmukhametov.auth.data.datasource

interface AuthDataSource {
    val currentUser: Any?

    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<Unit>

    suspend fun signInWithGoogle(idToken: String): Result<Unit>

    fun signOut()
}
