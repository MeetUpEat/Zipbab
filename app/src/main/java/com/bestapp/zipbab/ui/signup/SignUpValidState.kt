package com.bestapp.zipbab.ui.signup

enum class SignUpValidState(val num: Int) {
    None(0),
    UntilNickname(3),
    UntilEmail(9),
    UntilPassWord(11),
    All(12),
}