package ru.kazan.itis.bikmukhametov.feature.chatdetail.api.repository

interface GigaChatMessagesRepository {

    /**
     * Отправляет историю сообщений в GigaChat и возвращает текст ответа ассистента.
     * @param messages пары (role, content), например "user"/"assistant".
     */
    suspend fun sendChatCompletion(messages: List<Pair<String, String>>): Result<String>
}
