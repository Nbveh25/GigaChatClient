package ru.kazan.itis.bikmukhametov.feature.register.api.usecase

import ru.kazan.itis.bikmukhametov.feature.register.api.validation.RegistrationValidationResult

interface ValidateRegistrationUseCase {
    operator fun invoke(
        email: String,
        password: String,
        confirmPassword: String
    ): RegistrationValidationResult
}
