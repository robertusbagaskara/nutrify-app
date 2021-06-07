package com.yusril.nutrify.core.utils

import com.yusril.nutrify.core.data.source.firebase.profile.response.UserResponse
import com.yusril.nutrify.core.data.source.firebase.recommendation.response.RecommendationListResponse
import com.yusril.nutrify.core.data.source.firebase.statistics.response.CaloryPerDayResponse
import com.yusril.nutrify.core.data.source.firebase.statistics.response.ListStatisticsResponse
import com.yusril.nutrify.core.domain.model.*

object DataMapper {

    fun mapUserResponseToDomain(input: UserResponse) =
        User(
            birth_date = input.birth_date,
            height = input.height,
            weight = input.weight,
            gender = input.gender
        )

    fun mapCaloryDayResponsetoDomain(input: List<CaloryPerDayResponse>) : List<CaloryPerDay> {
        val list = ArrayList<CaloryPerDay>()
        input.map {
            val item = CaloryPerDay(
                budget = it.budget,
                total = it.total,
                remaining = it.remaining,
                day_name = it.day_name,
                date = it.date
            )
            list.add(item)
        }
        return list
    }

    fun mapRecommendationToDomain(input: List<RecommendationListResponse>) : List<RecommendationList> {
        val list = ArrayList<RecommendationList>()
        input.map {
            val item = RecommendationList(
                name = it.name,
                calories = it.calories
            )
            list.add(item)
        }
        return list
    }

    fun mapListStatisticToDomain(input: List<ListStatisticsResponse>) : List<ListStatistics> {
        val list = ArrayList<ListStatistics>()
        input.map {
            val foodItems = ArrayList<Food>()
            it.foods.map { i ->
                val foodItem = Food(
                    name = i.name
                )
                foodItems.add(foodItem)

            }
            val item = ListStatistics(
                foods = foodItems,
                total_calories = it.total_calories,
                time = it.time
            )
            list.add(item)
        }
        return list
    }


}