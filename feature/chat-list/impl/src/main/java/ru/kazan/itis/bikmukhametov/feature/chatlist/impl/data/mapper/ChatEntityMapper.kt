package ru.kazan.itis.bikmukhametov.feature.chatlist.impl.data.mapper

import ru.kazan.itis.bikmukhametov.database.chatlist.entity.ChatEntity
import ru.kazan.itis.bikmukhametov.feature.chatlist.api.model.ChatModel

internal fun ChatEntity.asChatModel(): ChatModel =
    ChatModel(
        id = id,
        title = title,
        createdAtEpochMillis = createdAtEpochMillis,
    )
