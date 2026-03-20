package ru.kazan.itis.bikmukhametov.feature.auth.impl.domain.usecase

import ru.kazan.itis.bikmukhametov.feature.auth.api.repository.AuthRepository
import ru.kazan.itis.bikmukhametov.feature.auth.api.usecase.SignInWithEmailPasswordUseCase
import javax.inject.Inject

class SignInWithEmailPasswordUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
): SignInWithEmailPasswordUseCase {
    override suspend operator fun invoke(email: String, password: String): Result<Unit> =
        authRepository.signInWithEmailAndPassword(email, password)
}
