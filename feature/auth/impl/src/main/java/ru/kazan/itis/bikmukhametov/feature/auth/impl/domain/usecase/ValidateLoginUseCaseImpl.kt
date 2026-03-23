package ru.kazan.itis.bikmukhametov.feature.auth.impl.domain.usecase

import jakarta.inject.Inject
import ru.kazan.itis.bikmukhametov.common.util.resource.StringResourceProvider
import ru.kazan.itis.bikmukhametov.feature.auth.api.usecase.ValidateLoginUseCase
import ru.kazan.itis.bikmukhametov.feature.auth.api.validation.AuthValidationResult
import ru.kazan.itis.bikmukhametov.feature.auth.api.validation.InputValidator
import ru.kazan.itis.bikmukhametov.feature.auth.impl.R

class ValidateLoginUseCaseImpl @Inject constructor(
    private val inputValidator: InputValidator,
    private val stringProvider: StringResourceProvider
) : ValidateLoginUseCase {

    override fun invoke(email: String, password: String): AuthValidationResult {
        val emailErr = if (email.isNotEmpty() && !inputValidator.isValidEmail(email)) {
            stringProvider.getString(R.string.error_invalid_email)
        } else null

        val passwordValidation = inputValidator.validatePassword(password)
        val passwordErr = if (password.isNotEmpty()) {
            (passwordValidation as? InputValidator.ValidationResult.Failure)?.message
        } else null

        val isFilled = email.isNotBlank() && password.isNotBlank()

        return AuthValidationResult(
            emailError = emailErr,
            passwordError = passwordErr,
            isValid = isFilled && emailErr == null && passwordErr == null
        )
    }
}