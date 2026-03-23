package ru.kazan.itis.bikmukhametov.gigachat.navigation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import ru.kazan.itis.bikmukhametov.api.usecase.GetAppThemeUseCase
import ru.kazan.itis.bikmukhametov.api.usecase.SetAppThemeUseCase
import ru.kazan.itis.bikmukhametov.database.chatlist.repository.ChatRepository

@HiltViewModel
class AppNavViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val getAppThemeUseCase: GetAppThemeUseCase,
    private val setAppThemeUseCase: SetAppThemeUseCase,
) : ViewModel() {

    private val _isDarkTheme = MutableStateFlow(getAppThemeUseCase())
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    suspend fun createNewChat(): String = chatRepository.createChat()

    fun setTheme(isDarkTheme: Boolean) {
        setAppThemeUseCase(isDarkTheme)
        _isDarkTheme.value = isDarkTheme
    }
}
