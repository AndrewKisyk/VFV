package com.wrapper.composechat.auth

object AuthValidation {

    /**
     * Mirrors [com.plstudio.a123.vfv.presenters.AutorizationPresenter.validate]:
     * age must be present, 12–17 inclusive, and sex must be chosen.
     */
    /** `true` when [ageInput] parses to 12–17 inclusive (single source of truth with [validate]). */
    fun isAgeInputValid(ageInput: String): Boolean {
        if (ageInput.isBlank()) return false
        val age = ageInput.toIntOrNull() ?: return false
        return age in 12..17
    }

    fun validate(ageInput: String, selectedSex: Sex?): Result {
        var ageError = false
        var sexError = false

        if (ageInput.isBlank()) {
            ageError = true
        } else {
            ageError = !isAgeInputValid(ageInput)
        }
        if (selectedSex == null) {
            sexError = true
        }
        return Result(ageError = ageError, sexError = sexError, isValid = !ageError && !sexError)
    }

    data class Result(
        val ageError: Boolean,
        val sexError: Boolean,
        val isValid: Boolean,
    )
}
