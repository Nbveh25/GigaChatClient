package ru.kazan.itis.bikmukhametov.api.usecase

interface UpdateUserNameUseCase {
    suspend operator fun invoke(name: String): Result<Unit>
}

