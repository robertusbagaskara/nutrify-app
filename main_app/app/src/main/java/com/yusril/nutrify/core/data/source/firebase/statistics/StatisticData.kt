package com.yusril.nutrify.core.data.source.firebase.statistics

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.yusril.nutrify.core.data.Resource
import com.yusril.nutrify.core.data.source.firebase.profile.response.UserResponse
import com.yusril.nutrify.core.data.source.firebase.statistics.response.FoodResponse
import com.yusril.nutrify.core.data.source.firebase.statistics.response.ListStatisticsResponse
import com.yusril.nutrify.core.domain.model.ListStatistics
import com.yusril.nutrify.core.domain.model.User
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class StatisticData {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun setStatistic(id: String, date: String ,listStatistics: ListStatistics): Resource<Boolean> =
        suspendCoroutine { cont ->
            db.collection("statistics").document(id).collection(date).document()
                .set(listStatistics)
                .addOnSuccessListener {
                    cont.resume(Resource.Success(true))
                }
                .addOnFailureListener {
                    cont.resume(Resource.Error(it.message))
                }
        }

    @Suppress("UNCHECKED_CAST")
    suspend fun getStatisticToday(id: String, date: String): Resource<List<ListStatisticsResponse>> =
        suspendCoroutine { cont ->
            db.collection("statistics").document(id).collection(date)
                .get()
                .addOnSuccessListener {
                    val todayStats = ArrayList<ListStatisticsResponse>()
                    for (stat in  it) {
                        val item = stat.toObject<ListStatisticsResponse>()
                        val food = ArrayList<FoodResponse>()
                        item.foods.map { item ->
                            val foodItem = FoodResponse(
                                name = item.name
                            )
                            food.add(foodItem)
                        }
                        val listStats = ListStatisticsResponse(
                            foods = food,
                            total_calories = item.total_calories,
                            time = item.time
                        )
                        todayStats.add(listStats)
                    }
                    try {
                        cont.resume(Resource.Success(todayStats) as Resource<List<ListStatisticsResponse>>)
                    } catch (e: Exception) {
                        cont.resume(Resource.Error(e.message))
                    }
                }
                .addOnFailureListener {
                    cont.resume(Resource.Error(it.message))
                }
        }
}