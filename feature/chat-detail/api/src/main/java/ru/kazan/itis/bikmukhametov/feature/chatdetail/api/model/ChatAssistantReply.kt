package ru.kazan.itis.bikmukhametov.feature.chatdetail.api.model

/**
 * Ответ модели GigaChat; [functionsStateId] нужно сохранять и отдавать
 * в следующих запросах (см. документацию functions_state_id).
 */
data class ChatAssistantReply(
    val content: String,
    val functionsStateId: String? = null,
)
