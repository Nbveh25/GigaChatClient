package ru.kazan.itis.bikmukhametov.auth.data.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import jakarta.inject.Inject
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
    }

    override suspend fun signInWithGoogle(idToken: String): Result<Unit> = runCatchingCancelable {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).await()
    }


    override fun signOut() {
        firebaseAuth.signOut()
    }
}