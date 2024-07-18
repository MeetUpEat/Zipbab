package com.bestapp.zipbab.ui.home

sealed interface NavDestination {
    data object Default: NavDestination
    data object Login: NavDestination
    data object Recruitment: NavDestination
}