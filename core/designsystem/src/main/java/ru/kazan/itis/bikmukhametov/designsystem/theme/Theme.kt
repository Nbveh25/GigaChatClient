package ru.kazan.itis.bikmukhametov.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AppPrimaryDark, // Используем бирюзовый акцент
    background = DarkBackground,
    surface = SurfaceDark,
    onPrimary = OnPrimaryDark,
    onBackground = OnBackgroundDark,
    onSurface = OnBackgroundDark,
    surfaceVariant = SurfaceVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = AppPrimary, // Используем бирюзовый акцент
    background = LightBackground,
    surface = SurfaceLight,
    onPrimary = OnPrimaryLight,
    onBackground = OnBackgroundLight,
    onSurface = OnBackgroundLight,
    surfaceVariant = SurfaceVariantLight
)

@Composable
fun GigaChatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
