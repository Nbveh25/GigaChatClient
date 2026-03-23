package ru.kazan.itis.bikmukhametov.api.usecase

import ru.kazan.itis.bikmukhametov.api.model.UserModel

interface GetUserProfileUseCase {
    suspend operator fun invoke(): Result<UserModel>
}
