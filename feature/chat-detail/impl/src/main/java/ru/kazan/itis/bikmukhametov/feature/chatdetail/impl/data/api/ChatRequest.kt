package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.data.api

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.EncodeDefault.Mode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageDto(
    val role: String,
    val content: String,
    @SerialName("functions_state_id") val functionsStateId: String? = null,
)

@Serializable
data class ChatBuiltinFunction(
    val name: String,
)

@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<ChatMessageDto>,
    val stream: Boolean = false,
    @SerialName("function_call")
    @EncodeDefault(Mode.ALWAYS)
    val functionCall: String = "auto",
    val functions: List<ChatBuiltinFunction>? = null,
) {
    companion object {
        const val DEFAULT_MODEL = "GigaChat"
    }
}
