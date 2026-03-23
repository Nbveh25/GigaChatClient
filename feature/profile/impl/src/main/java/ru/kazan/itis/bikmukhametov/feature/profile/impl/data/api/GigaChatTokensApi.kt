package ru.kazan.itis.bikmukhametov.feature.profile.impl.data.api

import retrofit2.http.GET

internal interface GigaChatTokensApi {
    @GET("api/v1/balance")
    suspend fun getTokensBalance(): BalanceResponse
}
