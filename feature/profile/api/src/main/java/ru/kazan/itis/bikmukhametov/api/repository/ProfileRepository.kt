package ru.kazan.itis.bikmukhametov.api.repository

import ru.kazan.itis.bikmukhametov.api.model.UserModel
import ru.kazan.itis.bikmukhametov.api.model.TokensCountModel
import java.io.InputStream

interface ProfileRepository {

    suspend fun getUserProfile(): Result<UserModel>

    suspend fun updateUserName(name: String): Result<Unit>

    suspend fun uploadProfilePhoto(
        inputStream: InputStream,
        fileName: String
    ): Result<String>

    suspend fun getTokensBalance(): Result<TokensCountModel>

}