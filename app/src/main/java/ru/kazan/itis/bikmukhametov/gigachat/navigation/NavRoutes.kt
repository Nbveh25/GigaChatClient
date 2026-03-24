package ru.kazan.itis.bikmukhametov.gigachat.navigation

object NavRoutes {
    const val Auth = "auth"
    const val Register = "register"
    const val ChatList = "chats"
    const val Chat = "chat/{chatId}?imageGeneration={imageGeneration}"
    const val Profile = "profile"

    fun chat(chatId: String, imageGeneration: Boolean = false): String =
        "chat/$chatId?imageGeneration=$imageGeneration"
}
