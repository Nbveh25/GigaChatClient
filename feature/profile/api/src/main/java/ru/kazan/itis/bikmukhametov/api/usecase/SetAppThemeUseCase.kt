package ru.kazan.itis.bikmukhametov.api.usecase

interface SetAppThemeUseCase {
    operator fun invoke(isDarkTheme: Boolean)
}
