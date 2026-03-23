package ru.kazan.itis.bikmukhametov.feature.register.impl.domain.usecase

import jakarta.inject.Inject
import ru.kazan.itis.bikmukhametov.common.util.resource.StringResourceProvider
import ru.kazan.itis.bikmukhametov.feature.auth.api.validation.InputValidator
import ru.kazan.itis.bikmukhametov.feature.register.api.usecase.ValidateRegistrationUseCase
import ru.kazan.itis.bikmukhametov.feature.register.api.validation.RegistrationValidationResult
import ru.kazan.itis.bikmukhametov.feature.register.impl.R

class ValidateRegistrationUseCaseImpl @Inject constructor(
    private val inputValidator: InputValidator,
    private val stringProvider: StringResourceProvider
): ValidateRegistrationUseCase {
    override fun invoke(
        email: String,
        password: String,
        confirmPassword: String
    ): RegistrationValidationResult {

        val emailErr = if (email.isNotEmpty() && !inputValidator.isValidEmail(email)) {
            stringProvider.getString(R.string.register_error_invalid_email)
        } else null

        val passwordValidation = inputValidator.validatePassword(password)
        val passwordErr = (passwordValidation as? InputValidator.ValidationResult.Failure)?.message

        val confirmErr = if (confirmPassword.isNotEmpty() && confirmPassword != password) {
            stringProvider.getString(R.string.register_error_passwords_not_match)
        } else null

        val isAllFilled = email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()

        return RegistrationValidationResult(
            emailError = emailErr,
            passwordError = passwordErr,
            confirmPasswordError = confirmErr,
            isValid = isAllFilled && emailErr == null && passwordErr == null && confirmErr == null
        )

    }
}