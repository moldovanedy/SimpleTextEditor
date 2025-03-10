package com.example.simpletexteditor.cloudmanager

import com.example.simpletexteditor.R

class UserValidations {
    companion object {
        private val regex = Regex("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")

        fun validateEmail(email: String): Boolean {
            val matches = regex.find(email) ?: return false

            return !(matches.groups.isEmpty() || matches.groups.size > 1 || matches.groups[0]?.value != email)
        }

        /**
         * Returns the ID of the error message from strings.xml or null if no error occurred.
         */
        fun validatePassword(password: String): Int? {
            if (password.length < 6) {
                return R.string.password_error_short
            }
            if (password.length > 64) {
                return R.string.password_error_long
            }

            if (!password.any { c -> c.isDigit() }) {
                return R.string.password_error_needs_digits
            }

            return if (password.any { c -> c.isUpperCase() })
                null
            else
                R.string.password_error_needs_uppercase
        }
    }
}
