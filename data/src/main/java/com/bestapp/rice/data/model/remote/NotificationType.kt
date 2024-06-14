package com.bestapp.rice.data.model.remote

sealed interface NotificationType { //muti-recyclerView 구현을 위한 클래스

    data class MainNotification( //관리자 전용 알림 데이터 형식
        val dec: String,
        val uploadDate: String
    ) : NotificationType {
        constructor() : this("", "")

        // TODO - 11. 튜터 님이라면 빈 값을 위해 변수가 아닌 empty 함수를 companion object에 만들 것이다.
        companion object {
            fun empty(): MainNotification = MainNotification(
                dec = "",
                uploadDate ="",
            )
        }
    }

    data class UserNotification( //유저 전용 알림 데이터 형식
        val dec: String,
        val uploadDate: String
    ) : NotificationType {
        constructor() : this("", "")
    }
}