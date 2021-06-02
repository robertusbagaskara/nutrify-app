package com.yusril.nutrify.core.utils

import com.yusril.nutrify.core.data.source.firebase.profile.response.UserResponse
import com.yusril.nutrify.core.data.source.firebase.statistics.response.ListStatisticsResponse
import com.yusril.nutrify.core.domain.model.Food
import com.yusril.nutrify.core.domain.model.ListStatistics
import com.yusril.nutrify.core.domain.model.User

object DataMapper {

    fun mapUserResponseToDomain(input: UserResponse) =
        User(
            birth_date = input.birth_date,
            height = input.height,
            weight = input.weight,
            gender = input.gender
        )

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