package com.yusril.nutrify.core.domain.repository

import com.yusril.nutrify.core.data.Resource
import com.yusril.nutrify.core.data.source.firebase.statistics.response.ListStatisticsResponse
import com.yusril.nutrify.core.domain.model.ListStatistics
import com.yusril.nutrify.core.domain.model.User

interface INutrifyRepository {

    suspend fun getProfile(id: String): Resource<User>

    suspend fun setProfile(id: String, user: User): Resource<Boolean>

    suspend fun setStatistic(id: String, date: String, listStatistics: ListStatistics): Resource<Boolean>

    suspend fun getStatisticToday(id: String, date: String): Resource<List<ListStatistics>>

}