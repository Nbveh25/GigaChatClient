package ru.kazan.itis.bikmukhametov.network.auth.api

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface GigaChatAuthApi {

    @FormUrlEncoded
    @POST("api/v2/oauth")
    suspend fun getAccessToken(
        @Field("scope") scope: String = "GIGACHAT_API_PERS",
    ): TokenResponse
}
