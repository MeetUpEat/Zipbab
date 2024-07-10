package com.bestapp.zipbab.ui.signup

data class InputValidState (
    val nickname: Boolean,
    val email: Boolean,
    val password: Boolean,
    val passwordCompare: Boolean,
    val term: Boolean,
) {
    fun isValid(): Boolean = nickname && email && password && passwordCompare && term
}
