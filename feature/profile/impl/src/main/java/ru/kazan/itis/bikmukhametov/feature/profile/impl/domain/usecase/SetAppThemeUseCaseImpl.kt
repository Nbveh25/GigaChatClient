package ru.kazan.itis.bikmukhametov.feature.profile.impl.domain.usecase

import javax.inject.Inject
import ru.kazan.itis.bikmukhametov.api.repository.AppThemeRepository
import ru.kazan.itis.bikmukhametov.api.usecase.SetAppThemeUseCase

internal class SetAppThemeUseCaseImpl @Inject constructor(
    private val appThemeRepository: AppThemeRepository,
) : SetAppThemeUseCase {
    override fun invoke(isDarkTheme: Boolean) {
        appThemeRepository.setDarkThemeEnabled(isDarkTheme)
    }
}
