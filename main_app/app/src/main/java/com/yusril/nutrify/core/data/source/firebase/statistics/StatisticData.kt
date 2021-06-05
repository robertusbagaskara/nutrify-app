package com.yusril.nutrify.core.data.source.firebase.statistics

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.yusril.nutrify.core.data.Resource
import com.yusril.nutrify.core.data.source.firebase.statistics.response.CaloryPerDayResponse
import com.yusril.nutrify.core.data.source.firebase.statistics.response.FoodResponse
import com.yusril.nutrify.core.data.source.firebase.statistics.response.ListStatisticsResponse
import com.yusril.nutrify.core.domain.model.CaloryPerDay
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
                        item.foods.map { itemFood ->
                            val foodItem = FoodResponse(
                                name = itemFood.name
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


    suspend fun setTotalCaloriesPerDay(
        id: String,
        date: String,
        total_calories: CaloryPerDay
    ): Resource<Boolean> =
        suspendCoroutine { cont ->
            db.collection("calories_per_day").document(id).collection("date").document(date)
                .set(total_calories)
                .addOnSuccessListener {
                    cont.resume(Resource.Success(true))
                }
                .addOnFailureListener {
                    cont.resume(Resource.Error(it.message))
                }
        }



    suspend fun getTotalCaloriesPerDay(
        id: String,
        dateInput: String,
        lowerLimit: Int,
        upperLimit: Int
    ): Resource<List<CaloryPerDayResponse>> =
        suspendCoroutine { cont ->
            db.collection("calories_per_day").document(id).collection("date")
                .whereGreaterThanOrEqualTo("date", lowerLimit)
                .whereLessThanOrEqualTo("date", upperLimit)
                .orderBy("date", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener {
                    Log.v("TEST", "TEST")
                    Log.d("TAG", it.metadata.toString())
                    val listCal = ArrayList<CaloryPerDayResponse>()
                    for (stat in it) {
                        Log.d("TAG", "${stat.id} => ${stat.data}")
                        val item = stat.toObject<CaloryPerDayResponse>()
                        val itemCal = CaloryPerDayResponse(
                            total = item.total,
                            day_name = item.day_name,
                            date = item.date
                        )
                        listCal.add(itemCal)
                    }
                    try {
                        Log.v("listStats", listCal.toString())
                        cont.resume(Resource.Success(listCal) as Resource<List<CaloryPerDayResponse>>)
                    } catch (e: Exception) {
                        cont.resume(Resource.Error(e.message))
                    }
                }
                .addOnFailureListener {
                    cont.resume(Resource.Error(it.message))
                }
        }
}