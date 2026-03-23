package ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase

import javax.inject.Inject
import ru.kazan.itis.bikmukhametov.api.model.TokensCountModel
import ru.kazan.itis.bikmukhametov.api.repository.ProfileRepository
import ru.kazan.itis.bikmukhametov.api.usecase.GetTokensCountUseCase

internal class GetTokensCountUseCaseImpl @Inject constructor(
    private val profileRepository: ProfileRepository,
) : GetTokensCountUseCase {
    override suspend fun invoke(): Result<TokensCountModel> {
        return profileRepository.getTokensBalance()
    }
}
