package ru.kazan.itis.bikmukhametov.feature.register.api.validation

data class RegistrationValidationResult(
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isValid: Boolean = false
)
