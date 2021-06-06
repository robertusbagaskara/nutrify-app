package com.yusril.nutrify.core.data.source.firebase

import com.yusril.nutrify.core.data.Resource
import com.yusril.nutrify.core.data.source.firebase.profile.ProfileData
import com.yusril.nutrify.core.data.source.firebase.profile.response.UserResponse
import com.yusril.nutrify.core.data.source.firebase.recommendation.RecommendationData
import com.yusril.nutrify.core.data.source.firebase.recommendation.response.RecommendationListResponse
import com.yusril.nutrify.core.data.source.firebase.statistics.StatisticData
import com.yusril.nutrify.core.data.source.firebase.statistics.response.CaloryPerDayResponse
import com.yusril.nutrify.core.data.source.firebase.statistics.response.ListStatisticsResponse
import com.yusril.nutrify.core.domain.model.CaloryPerDay
import com.yusril.nutrify.core.domain.model.ListStatistics
import com.yusril.nutrify.core.domain.model.User

class FirebaseDataSource(
    private val profileData: ProfileData,
    private val statisticData: StatisticData,
    private val recommendationData: RecommendationData
) {

    suspend fun getProfile(id: String): Resource<UserResponse> =
        profileData.getProfile(id)

    suspend fun setProfile(id: String, user: User) =
        profileData.setProfile(id, user)

    suspend fun setStatistic(
        id: String,
        date: String,
        time: String,
        listStatistics: ListStatistics
    ): Resource<Boolean> =
        statisticData.setStatistic(id, date, time, listStatistics)

    suspend fun getStatisticToday(id: String, date: String) : Resource<List<ListStatisticsResponse>> {
        return statisticData.getStatisticToday(id, date)
    }



    suspend fun setTotalCaloriesPerDay(
        id: String,
        date: String,
        total_calories: CaloryPerDay
    ): Resource<Boolean> =
        statisticData.setTotalCaloriesPerDay(id, date, total_calories)

    suspend fun getTotalCaloriesPerDay(id: String, date: String, lowerLimit: Int, upperLimit: Int) : Resource<List<CaloryPerDayResponse>> {
        return statisticData.getTotalCaloriesPerDay(id, date, lowerLimit, upperLimit)
    }

    suspend fun getRecommendation(minusCalories: Int): Resource<List<RecommendationListResponse>> {
        return recommendationData.getRecommendation(minusCalories)
    }
}