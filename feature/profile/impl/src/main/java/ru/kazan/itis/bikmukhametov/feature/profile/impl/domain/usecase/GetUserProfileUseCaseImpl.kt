package ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase

import ru.kazan.itis.bikmukhametov.api.model.UserModel
import ru.kazan.itis.bikmukhametov.api.repository.ProfileRepository
import ru.kazan.itis.bikmukhametov.api.usecase.GetUserProfileUseCase
import javax.inject.Inject

internal class GetUserProfileUseCaseImpl @Inject constructor(
    private val profileRepository: ProfileRepository,
) : GetUserProfileUseCase {
    override suspend fun invoke(): Result<UserModel> = profileRepository.getUserProfile()
}
