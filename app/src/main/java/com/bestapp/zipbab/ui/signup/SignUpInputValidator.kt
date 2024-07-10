package com.bestapp.zipbab.ui.signup

import android.content.Context
import com.bestapp.zipbab.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.regex.Pattern
import javax.inject.Inject

class SignUpInputValidator @Inject constructor(@ApplicationContext context: Context) {

    private val emailRegex =
        """^[_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\.[A-Za-z0-9]+)*(\.[A-Za-z]{3,})$"""

    private val allowNicknameLength =
        context.resources.getInteger(R.integer.min_nickname_length)..context.resources.getInteger(R.integer.max_nickname_length)

    private val allowedPasswordLength =
        context.resources.getInteger(R.integer.min_password_length)..context.resources.getInteger(R.integer.max_password_length)

    private val allowedEmailLength =
        context.resources.getInteger(R.integer.min_email_length)..context.resources.getInteger(R.integer.max_email_length)

    fun isValid(
        nickname: String,
        email: String,
        password: String,
        passwordCompare: String,
        termChecked: Boolean
    ): InputValidState {
        return InputValidState(
            nickname = checkNicknameValid(nickname),
            email = checkEmailValid(email),
            password = checkPasswordValid(password),
            passwordCompare = checkPasswordCompareValid(password, passwordCompare),
            term = checkTermValid(termChecked),
        )
    }

    private fun checkNicknameValid(nickname: String): Boolean =
        nickname.length in allowNicknameLength

    private fun checkEmailValid(email: String): Boolean =
        email.length in allowedEmailLength && Pattern.matches(emailRegex, email)

    private fun checkPasswordValid(password: String): Boolean {
        return password.length in allowedPasswordLength && password.isNotBlank()
    }
    private fun checkPasswordCompareValid(password: String, passwordCompare: String): Boolean {
        return password == passwordCompare
    }

    private fun checkTermValid(termChecked: Boolean): Boolean = termChecked
}