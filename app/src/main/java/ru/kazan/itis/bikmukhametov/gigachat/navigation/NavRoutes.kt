package ru.kazan.itis.bikmukhametov.gigachat.navigation

object NavRoutes {
    const val Auth = "auth"
    const val Register = "register"
    const val ChatList = "chats"
    const val Chat = "chat/{chatId}"
    const val Profile = "profile"
    const val Images = "images"

    fun chat(chatId: String): String = "chat/$chatId"
}
