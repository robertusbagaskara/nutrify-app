package com.yusril.nutrify.core.data.source.firebase

import com.yusril.nutrify.core.data.Resource
import com.yusril.nutrify.core.data.source.firebase.profile.ProfileData
import com.yusril.nutrify.core.data.source.firebase.profile.response.UserResponse
import com.yusril.nutrify.core.data.source.firebase.statistics.StatisticData
import com.yusril.nutrify.core.data.source.firebase.statistics.response.ListStatisticsResponse
import com.yusril.nutrify.core.domain.model.ListStatistics
import com.yusril.nutrify.core.domain.model.User

class FirebaseDataSource(
    private val profileData: ProfileData,
    private val statisticData: StatisticData
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
}