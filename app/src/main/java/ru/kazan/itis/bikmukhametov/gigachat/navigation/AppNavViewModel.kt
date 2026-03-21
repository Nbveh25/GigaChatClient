package ru.kazan.itis.bikmukhametov.gigachat.navigation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import ru.kazan.itis.bikmukhametov.database.chatlist.repository.ChatRepository

@HiltViewModel
class AppNavViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
) : ViewModel() {

    suspend fun createNewChat(): String = chatRepository.createChat()
}
