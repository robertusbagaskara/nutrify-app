package com.yusril.nutrify.core.data.source.firebase.statistics.response

data class ListStatisticsResponse(
    val foods: ArrayList<FoodResponse> = arrayListOf(),
    var total_calories: Int = 0,
    var time : String = ""
)