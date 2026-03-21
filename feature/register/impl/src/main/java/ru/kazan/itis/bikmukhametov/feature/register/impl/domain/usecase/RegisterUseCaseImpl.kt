package ru.kazan.itis.bikmukhametov.feature.register.impl.domain.usecase

import javax.inject.Inject
import ru.kazan.itis.bikmukhametov.feature.auth.api.repository.AuthRepository
import ru.kazan.itis.bikmukhametov.feature.register.api.usecase.RegisterUseCase

internal class RegisterUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
) : RegisterUseCase {
    override suspend fun invoke(email: String, password: String): Result<Unit> =
        authRepository.registerWithEmailAndPassword(email.trim(), password)
}
