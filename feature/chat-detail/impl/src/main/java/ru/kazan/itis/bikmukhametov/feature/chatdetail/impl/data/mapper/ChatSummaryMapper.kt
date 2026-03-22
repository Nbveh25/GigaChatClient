package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.data.mapper

import ru.kazan.itis.bikmukhametov.database.chatlist.entity.ChatEntity
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.model.ChatSummaryModel

internal fun ChatEntity.toSummaryModel(): ChatSummaryModel =
    ChatSummaryModel(
        id = id,
        title = title,
        createdAtEpochMillis = createdAtEpochMillis,
    )
