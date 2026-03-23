package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.data.mapper

import ru.kazan.itis.bikmukhametov.database.chatlist.entity.ChatMessageEntity
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.model.ChatMessageModel

internal fun ChatMessageEntity.toModel(): ChatMessageModel =
    ChatMessageModel(
        id = id,
        chatId = chatId,
        role = role,
        text = text,
        createdAtEpochMillis = createdAtEpochMillis,
        functionsStateId = functionsStateId,
    )

internal fun ChatMessageModel.toEntity(): ChatMessageEntity =
    ChatMessageEntity(
        id = id,
        chatId = chatId,
        role = role,
        text = text,
        createdAtEpochMillis = createdAtEpochMillis,
        functionsStateId = functionsStateId,
    )
