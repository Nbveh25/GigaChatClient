package ru.kazan.itis.bikmukhametov.api.repository

interface AppThemeRepository {
    fun isDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(enabled: Boolean)
}
