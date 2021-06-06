package com.yusril.nutrify.core.domain.repository

import com.yusril.nutrify.core.data.Resource
import com.yusril.nutrify.core.data.source.firebase.recommendation.response.RecommendationListResponse
import com.yusril.nutrify.core.data.source.firebase.statistics.response.ListStatisticsResponse
import com.yusril.nutrify.core.domain.model.CaloryPerDay
import com.yusril.nutrify.core.domain.model.ListStatistics
import com.yusril.nutrify.core.domain.model.RecommendationList
import com.yusril.nutrify.core.domain.model.User

interface INutrifyRepository {

    suspend fun getProfile(id: String): Resource<User>

    suspend fun setProfile(id: String, user: User): Resource<Boolean>

    suspend fun setStatistic(id: String, date: String, time: String, listStatistics: ListStatistics): Resource<Boolean>

    suspend fun getStatisticToday(id: String, date: String): Resource<List<ListStatistics>>

    suspend fun setTotalCaloriesPerDay(id: String, date: String, total_calories: CaloryPerDay): Resource<Boolean>

    suspend fun getTotalCaloriesPerDay(id: String, date: String, lowerLimit: Int, upperLimit: Int): Resource<List<CaloryPerDay>>

    suspend fun getRecommendation(minusCalories: Int): Resource<List<RecommendationList>>

}