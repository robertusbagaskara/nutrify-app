package com.yusril.nutrify.core.data.source.firebase.statistics.response

data class CaloryPerDayResponse(
    var budget: Int = 0,
    var total: Int = 0,
    var remaining: Int = 0,
    var day_name: String = "",
    var date: Int = 0
)
