package com.yusril.nutrify.core.data.source.firebase.statistics.response

data class ListStatisticsResponse(
    val foods: List<FoodResponse>,
    var total_calories: Int = 0,
    var time : String = ""
)