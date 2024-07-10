package com.bestapp.zipbab.ui.login

import android.content.Context
import com.bestapp.zipbab.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.regex.Pattern
import javax.inject.Inject

class LoginValidator @Inject constructor(@ApplicationContext context: Context) {

    private val emailRegex =
        """^[_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\.[A-Za-z0-9]+)*(\.[A-Za-z]{3,})$"""

    private val allowNicknameLength =
        context.resources.getInteger(R.integer.min_nickname_length)..context.resources.getInteger(R.integer.max_nickname_length)

    private val allowedPasswordLength =
        context.resources.getInteger(R.integer.min_password_length)..context.resources.getInteger(R.integer.max_password_length)

    fun isValid(
        nickname: String,
        email: String,
        password: String,
        passwordCompare: String,
        termChecked: Boolean
    ): Boolean {
        return checkNicknameValid(nickname) && checkEmailValid(email) && checkPasswordValid(password, passwordCompare) && termChecked
    }

    private fun checkNicknameValid(nickname: String): Boolean = nickname.length in allowNicknameLength

    private fun checkEmailValid(email: String): Boolean = Pattern.matches(emailRegex, email)

    private fun checkPasswordValid(password: String, passwordCompare: String): Boolean {
        return password.length in allowedPasswordLength && password.isNotBlank() && password == passwordCompare
    }
}