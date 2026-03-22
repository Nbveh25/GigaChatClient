package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.presentation.item

import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.model.ChatMessageModel
import java.util.UUID

data class ChatMessageItem(
    val id: String = UUID.randomUUID().toString(),
    val role: ChatMessageRole,
    val text: String,
)

internal fun ChatMessageModel.toItem() = ChatMessageItem(
    id = id,
    role = if (role == "assistant") ChatMessageRole.Assistant else ChatMessageRole.User,
    text = text,
)
