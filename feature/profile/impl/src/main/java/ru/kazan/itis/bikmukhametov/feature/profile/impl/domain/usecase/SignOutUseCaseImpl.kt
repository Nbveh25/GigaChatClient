package ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase

import ru.kazan.itis.bikmukhametov.api.usecase.SignOutUseCase
import ru.kazan.itis.bikmukhametov.feature.auth.api.repository.AuthRepository
import javax.inject.Inject

class SignOutUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
) : SignOutUseCase {
    override suspend fun invoke() = authRepository.signOut()
}
