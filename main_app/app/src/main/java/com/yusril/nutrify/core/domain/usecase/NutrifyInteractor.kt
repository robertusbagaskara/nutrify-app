package com.yusril.nutrify.core.domain.usecase

import com.yusril.nutrify.core.data.Resource
import com.yusril.nutrify.core.domain.model.CaloryPerDay
import com.yusril.nutrify.core.domain.model.ListStatistics
import com.yusril.nutrify.core.domain.model.RecommendationList
import com.yusril.nutrify.core.domain.model.User
import com.yusril.nutrify.core.domain.repository.INutrifyRepository

class NutrifyInteractor(private val iNutrifyRepository: INutrifyRepository) : NutrifyUseCase {

    override suspend fun getProfile(id: String): Resource<User> =
        iNutrifyRepository.getProfile(id)

    override suspend fun setProfile(id: String, user: User): Resource<Boolean> =
        iNutrifyRepository.setProfile(id, user)

    override suspend fun setStatistic(id: String, date: String, time: String, listStatistics: ListStatistics): Resource<Boolean> =
        iNutrifyRepository.setStatistic(id, date, time, listStatistics)

    override suspend fun getStatisticToday(id: String, date: String): Resource<List<ListStatistics>> =
        iNutrifyRepository.getStatisticToday(id, date)

    override suspend fun getStatisticFood(id: String, lowerLimit: Int, upperLimit: Int): Resource<List<ListStatistics>> =
        iNutrifyRepository.getStatisticFood(id, lowerLimit, upperLimit)

    override suspend fun setTotalCaloriesPerDay(id: String, date: String, total_calories: CaloryPerDay): Resource<Boolean> =
        iNutrifyRepository.setTotalCaloriesPerDay(id, date, total_calories)

    override suspend fun getTotalCaloriesPerDay(id: String, date: String, lowerLimit: Int, upperLimit: Int): Resource<List<CaloryPerDay>> =
        iNutrifyRepository.getTotalCaloriesPerDay(id, date, lowerLimit, upperLimit)

    override suspend fun getRecommendation(minusCalories: Int): Resource<List<RecommendationList>> =
        iNutrifyRepository.getRecommendation(minusCalories)


}