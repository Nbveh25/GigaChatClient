package ru.kazan.itis.bikmukhametov.feature.auth.impl.data.repository

import javax.inject.Inject
import javax.inject.Singleton
import ru.kazan.itis.bikmukhametov.auth.data.datasource.AuthDataSource
import ru.kazan.itis.bikmukhametov.feature.auth.api.repository.AuthRepository

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
) : AuthRepository {
    override val currentUser: Any?
        get() = authDataSource.currentUser

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<Unit> =
        authDataSource.signInWithEmailAndPassword(email, password)

    override suspend fun signInWithGoogle(idToken: String): Result<Unit> =
        authDataSource.signInWithGoogle(idToken)

    override suspend fun registerWithEmailAndPassword(
        email: String,
        password: String,
    ): Result<Unit> = authDataSource.registerWithEmailAndPassword(email, password)

    override fun signOut() = authDataSource.signOut()
}