package com.bestapp.zipbab.ui.setting

/**
 * 인터넷 연결 상태가 좋지 않아 로딩이 지연되는 경우에 대응하기 위해 생성
 */
sealed interface LoadingState {
    data object Default: LoadingState
    data object OnLoading: LoadingState
    data object Done: LoadingState
}