package com.yusril.nutrify.core.domain.model

data class ListStatistics(
    val foods: MutableList<Food>,
    var total_calories: Int = 0,
    var time : String = "",
    var date: Int = 0
)