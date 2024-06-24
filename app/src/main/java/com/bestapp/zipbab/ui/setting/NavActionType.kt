package com.bestapp.zipbab.ui.setting

/**
 * Compose 기반의 Navigation Component로 migration 하기 전까지, Compose에서 Navigation Action을 호출하기 위한 용도로 사용
 */
enum class NavActionType {
    LOGIN, REGISTER, MEETING,
    PROFILE, ALERT;
}