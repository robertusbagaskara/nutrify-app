package com.yusril.nutrify.core.data.source.firebase.statistics

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.yusril.nutrify.core.data.Resource
import com.yusril.nutrify.core.data.source.firebase.statistics.response.FoodResponse
import com.yusril.nutrify.core.data.source.firebase.statistics.response.ListStatisticsResponse
import com.yusril.nutrify.core.domain.model.ListStatistics
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Suppress("CAST_NEVER_SUCCEEDS")
class StatisticData {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun setStatistic(
        id: String,
        date: String,
        time: String,
        listStatistics: ListStatistics
    ): Resource<Boolean> =
        suspendCoroutine { cont ->
            db.collection("statistics").document(id).collection(date).document(time)
                .set(listStatistics)
                .addOnSuccessListener {
                    cont.resume(Resource.Success(true))
                }
                .addOnFailureListener {
                    cont.resume(Resource.Error(it.message))
                }
        }

    suspend fun getStatisticToday(
        id: String,
        date: String
    ): Resource<List<ListStatisticsResponse>> =
        suspendCoroutine { cont ->
            db.collection("statistics").document(id).collection(date)
                .get()
                .addOnSuccessListener {
                    Log.v("TEST", "TEST")
                    val todayStats = ArrayList<ListStatisticsResponse>()
                    for (stat in it) {
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
                        Log.v("listStats", todayStats.toString())
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