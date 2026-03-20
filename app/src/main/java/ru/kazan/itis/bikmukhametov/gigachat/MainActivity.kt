package ru.kazan.itis.bikmukhametov.gigachat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import ru.kazan.itis.bikmukhametov.designsystem.theme.GigaChatTheme
import ru.kazan.itis.bikmukhametov.gigachat.navigation.AppNavigation

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GigaChatTheme {
                AppNavigation(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
