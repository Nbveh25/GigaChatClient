package ru.kazan.itis.bikmukhametov.feature.auth.api.usecase

interface SignInWithEmailPasswordUseCase {
    suspend operator fun invoke(email: String, password: String): Result<Unit>
}
