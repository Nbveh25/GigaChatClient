package ru.kazan.itis.bikmukhametov.feature.profile.impl.data.repository

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton
import ru.kazan.itis.bikmukhametov.api.repository.AppThemeRepository
import androidx.core.content.edit

@Singleton
internal class AppThemeRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) : AppThemeRepository {

    override fun isDarkThemeEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_DARK_THEME, false)
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        sharedPreferences.edit { putBoolean(KEY_DARK_THEME, enabled) }
    }

    private companion object {
        private const val KEY_DARK_THEME = "key_dark_theme"
    }
}
