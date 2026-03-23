package ru.kazan.itis.bikmukhametov.feature.auth.api.validation

data class AuthValidationResult(
    val emailError: String? = null,
    val passwordError: String? = null,
    val isValid: Boolean = false
)
