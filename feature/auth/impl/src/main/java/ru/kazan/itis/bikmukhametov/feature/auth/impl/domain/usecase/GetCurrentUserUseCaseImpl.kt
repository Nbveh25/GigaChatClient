package ru.kazan.itis.bikmukhametov.feature.auth.impl.domain.usecase

import javax.inject.Inject
import ru.kazan.itis.bikmukhametov.feature.auth.api.repository.AuthRepository
import ru.kazan.itis.bikmukhametov.feature.auth.api.usecase.GetCurrentUserUseCase

internal class GetCurrentUserUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
) : GetCurrentUserUseCase {

    override suspend fun invoke(): Any? {
        if (!authRepository.awaitCurrentUserPresent()) return null
        return authRepository.currentUser
    }
}
