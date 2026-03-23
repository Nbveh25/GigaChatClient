package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.data.api

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.POST

interface GigaChatMainApi {

    @POST("api/v1/chat/completions")
    suspend fun sendMessage(@Body request: ChatRequest): ChatResponse

    @GET("api/v1/files/{fileId}/content")
    @Headers("Accept: image/jpg")
    suspend fun downloadImage(@Path("fileId") fileId: String): ResponseBody
}
