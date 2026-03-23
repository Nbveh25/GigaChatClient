package ru.kazan.itis.bikmukhametov.feature.auth.api.usecase

import ru.kazan.itis.bikmukhametov.feature.auth.api.validation.AuthValidationResult

interface ValidateLoginUseCase {
    operator fun invoke(email: String, password: String): AuthValidationResult
}
