package com.yusril.nutrify.core.domain.usecase

import com.yusril.nutrify.core.data.Resource
import com.yusril.nutrify.core.domain.model.ListStatistics
import com.yusril.nutrify.core.domain.model.User

interface NutrifyUseCase {

    suspend fun getProfile(id: String): Resource<User>

    suspend fun setProfile(id: String, user: User): Resource<Boolean>

    suspend fun setStatistic(id: String, date: String, time: String, listStatistics: ListStatistics): Resource<Boolean>

    suspend fun getStatisticToday(id: String, date: String): Resource<List<ListStatistics>>

}