package com.bestapp.rice.data.model.remote

sealed interface Filter {

    /**
     * @param icon FireStore에 있는 Filter 아이콘 이미지 Uri
     * @param name 이름 ex) 파스타, 전, 구이, 샌드위치
     */
    data class Food(
        val icon: String,
        val name: String,
    ) : Filter

    /**
     * @param name 1인당 참여 비용 텍스트 ex) ~3만원, 3~5만원, 5~10만원, 10만원~
     * @param type 1인당 참여 비용 타입 ex) 1, 2, 3, 4
     */
    data class Cost(
        val name: Int,
        val type: Int,
    ) : Filter

}
