package ru.kazan.itis.bikmukhametov.feature.profile.impl.data.api

import kotlinx.serialization.Serializable

@Serializable
internal data class BalanceResponse(
    val balance: List<BalanceDto> = emptyList(),
)

@Serializable
internal data class BalanceDto(
    val usage: String? = null,
    val value: Long = 0L,
)
