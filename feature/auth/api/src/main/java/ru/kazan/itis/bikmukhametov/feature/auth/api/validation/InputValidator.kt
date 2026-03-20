package ru.kazan.itis.bikmukhametov.feature.auth.api.validation

interface InputValidator {
    fun isValidEmail(email: String): Boolean

    fun validatePassword(password: String): ValidationResult

    sealed class ValidationResult {
        object Success : ValidationResult()
        data class Failure(val message: String) : ValidationResult()
    }
}
