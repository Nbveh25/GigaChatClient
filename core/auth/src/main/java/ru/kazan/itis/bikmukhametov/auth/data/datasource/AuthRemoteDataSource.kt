package ru.kazan.itis.bikmukhametov.auth.data.datasource

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.tasks.await
import ru.kazan.itis.bikmukhametov.network.error.runCatchingCancelable

class AuthRemoteDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
): AuthDataSource {
    override val currentUser: Any?
        get() = firebaseAuth.currentUser


    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<Unit> = runCatchingCancelable {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
    }.map { }.normalizeNetworkErrors()

    override suspend fun signInWithGoogle(idToken: String): Result<Unit> = runCatchingCancelable {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).await()
    }.map { }.normalizeNetworkErrors()

    override suspend fun registerWithEmailAndPassword(
        email: String,
        password: String,
    ): Result<Unit> = runCatchingCancelable {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
    }.map { }.normalizeNetworkErrors()

    override fun signOut() {
        firebaseAuth.signOut()
    }
}

private fun <T> Result<T>.normalizeNetworkErrors(): Result<T> = fold(
    onSuccess = { Result.success(it) },
    onFailure = { e ->
        Result.failure(
            if (e is FirebaseNetworkException) IOException(e.message, e) else e,
        )
    },
)