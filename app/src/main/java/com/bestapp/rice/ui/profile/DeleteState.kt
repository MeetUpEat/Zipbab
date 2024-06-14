package com.bestapp.rice.ui.profile

sealed interface DeleteState {

    data object Default: DeleteState

    data object Pending: DeleteState

    data object Progress : DeleteState
    data object Complete : DeleteState

    data object Fail: DeleteState

}