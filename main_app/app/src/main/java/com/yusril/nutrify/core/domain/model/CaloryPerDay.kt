package com.yusril.nutrify.core.domain.model

data class CaloryPerDay(
    var budget: Int = 0,
    var total: Int = 0,
    var remaining: Int = 0,
    var day_name: String = "",
    var date: Int = 0
)
