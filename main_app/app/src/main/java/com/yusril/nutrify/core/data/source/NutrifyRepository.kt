package com.yusril.nutrify.core.data.source

import com.yusril.nutrify.core.data.Resource
import com.yusril.nutrify.core.data.source.firebase.FirebaseDataSource
import com.yusril.nutrify.core.domain.model.CaloryPerDay
import com.yusril.nutrify.core.domain.model.ListStatistics
import com.yusril.nutrify.core.domain.model.RecommendationList
import com.yusril.nutrify.core.domain.model.User
import com.yusril.nutrify.core.domain.repository.INutrifyRepository
import com.yusril.nutrify.core.utils.DataMapper

class NutrifyRepository(private val firebaseDataSource: FirebaseDataSource) : INutrifyRepository {

    override suspend fun setProfile(id: String, user: User): Resource<Boolean> =
        firebaseDataSource.setProfile(id, user)

    override suspend fun getProfile(id: String): Resource<User> {
        return when (val response = firebaseDataSource.getProfile(id)) {
            is Resource.Success -> Resource.Success(DataMapper.mapUserResponseToDomain(response.data))
            is Resource.Error -> Resource.Error(response.exception)
            is Resource.Loading -> Resource.Loading()
        }
    }


    override suspend fun setStatistic(id: String, date: String, time: String, listStatistics: ListStatistics): Resource<Boolean> =
        firebaseDataSource.setStatistic(id, date, time, listStatistics)

    override suspend fun getStatisticToday(id: String, date: String): Resource<List<ListStatistics>> {
        return when(val response = firebaseDataSource.getStatisticToday(id, date)) {
            is Resource.Success -> Resource.Success(DataMapper.mapListStatisticToDomain(response.data))
            is Resource.Error -> Resource.Error(response.exception)
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun getStatisticFood(id: String, lowerLimit: Int, upperLimit: Int): Resource<List<ListStatistics>> {
        return when(val response = firebaseDataSource.getStatisticFood(id, lowerLimit, upperLimit)) {
            is Resource.Success -> Resource.Success(DataMapper.mapListStatisticToDomain(response.data))
            is Resource.Error -> Resource.Error(response.exception)
            is Resource.Loading -> Resource.Loading()
        }
    }


    override suspend fun setTotalCaloriesPerDay(id: String, date: String, total_calories: CaloryPerDay): Resource<Boolean> =
        firebaseDataSource.setTotalCaloriesPerDay(id, date, total_calories)

    override suspend fun getTotalCaloriesPerDay(id: String, lowerLimit: Int, upperLimit: Int): Resource<List<CaloryPerDay>> {
        return when(val response = firebaseDataSource.getTotalCaloriesPerDay(id, lowerLimit, upperLimit)) {
            is Resource.Success -> Resource.Success(DataMapper.mapCaloryDayResponsetoDomain(response.data))
            is Resource.Error -> Resource.Error(response.exception)
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun getRecommendation(minusCalories: Int): Resource<List<RecommendationList>> {
        return when(val response = firebaseDataSource.getRecommendation(minusCalories)) {
            is Resource.Success -> Resource.Success(DataMapper.mapRecommendationToDomain(response.data))
            is Resource.Error -> Resource.Error(response.exception)
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun getFoodDataFromScan(foods: List<String>): Resource<List<RecommendationList>> {
        return when(val response = firebaseDataSource.getFoodDataFromScan(foods)) {
            is Resource.Success -> Resource.Success(DataMapper.mapRecommendationToDomain(response.data))
            is Resource.Error -> Resource.Error(response.exception)
            is Resource.Loading -> Resource.Loading()
        }
    }


}