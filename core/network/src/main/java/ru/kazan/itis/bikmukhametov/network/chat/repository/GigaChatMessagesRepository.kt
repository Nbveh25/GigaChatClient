package ru.kazan.itis.bikmukhametov.network.chat.repository

interface GigaChatMessagesRepository {

    /**
     * Отправляет историю сообщений в GigaChat и возвращает текст ответа ассистента.
     * @param messages пары (role, content), например "user"/"assistant".
     */
    suspend fun sendChatCompletion(messages: List<Pair<String, String>>): Result<String>
}
