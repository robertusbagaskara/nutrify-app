package com.yusril.nutrify.core.data.source.firebase.recommendation

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.yusril.nutrify.core.data.Resource
import com.yusril.nutrify.core.data.source.firebase.recommendation.response.RecommendationListResponse
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RecommendationData {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun getRecommendation(minusCalories: Int): Resource<List<RecommendationListResponse>> =
        suspendCoroutine { cont ->
            db.collection("foods")
                .whereLessThanOrEqualTo("calories", minusCalories)
                .orderBy("calories")
                .get()
                .addOnSuccessListener {
                    val list = ArrayList<RecommendationListResponse>()
                    for (item in it) {
                        val itemList = item.toObject<RecommendationListResponse>()
                        val itemListRec = RecommendationListResponse(
                            name = itemList.name,
                            calories = itemList.calories
                        )
                        list.add(itemListRec)
                    }
                    try {
                        cont.resume(Resource.Success(list) as Resource<List<RecommendationListResponse>>)
                    } catch (e: Exception) {
                        cont.resume(Resource.Error(e.message))
                    }
                }
                .addOnFailureListener {
                    cont.resume(Resource.Error(it.message))
                }
        }



    suspend fun getFoodDataFromScan(foods: List<String>):
            Resource<List<RecommendationListResponse>> =
        suspendCoroutine { cont ->
            db.collection("foods")
                .whereIn("name", foods)
                .get()
                .addOnSuccessListener {
                    val list = ArrayList<RecommendationListResponse>()
                    for (item in it) {
                        val itemList = item.toObject<RecommendationListResponse>()
                        val itemListRec = RecommendationListResponse(
                            name = itemList.name,
                            calories = itemList.calories
                        )
                        list.add(itemListRec)
                    }
                    try {
                        cont.resume(Resource.Success(list) as Resource<List<RecommendationListResponse>>)
                    } catch (e: Exception) {
                        cont.resume(Resource.Error(e.message))
                    }
                }
                .addOnFailureListener {
                    cont.resume(Resource.Error(it.message))
                }
        }
}