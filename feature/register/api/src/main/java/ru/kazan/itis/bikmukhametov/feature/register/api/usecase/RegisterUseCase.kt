package ru.kazan.itis.bikmukhametov.feature.register.api.usecase

fun interface RegisterUseCase {
    suspend operator fun invoke(email: String, password: String): Result<Unit>
}
