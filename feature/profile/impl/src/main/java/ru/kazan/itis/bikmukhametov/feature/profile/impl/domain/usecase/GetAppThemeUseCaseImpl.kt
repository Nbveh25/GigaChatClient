package ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase

import javax.inject.Inject
import ru.kazan.itis.bikmukhametov.api.repository.AppThemeRepository
import ru.kazan.itis.bikmukhametov.api.usecase.GetAppThemeUseCase

internal class GetAppThemeUseCaseImpl @Inject constructor(
    private val appThemeRepository: AppThemeRepository,
) : GetAppThemeUseCase {
    override fun invoke(): Boolean = appThemeRepository.isDarkThemeEnabled()
}
