package ru.kazan.itis.bikmukhametov.network.chat.api

import retrofit2.http.Body
import retrofit2.http.POST

interface GigaChatMainApi {

    @POST("api/v1/chat/completions")
    suspend fun sendMessage(@Body request: ChatRequest): ChatResponse
}
