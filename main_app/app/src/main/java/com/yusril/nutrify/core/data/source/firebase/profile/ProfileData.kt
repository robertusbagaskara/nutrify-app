package com.yusril.nutrify.core.data.source.firebase.profile

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.yusril.nutrify.core.data.Resource
import com.yusril.nutrify.core.data.source.firebase.response.UserResponse
import com.yusril.nutrify.core.domain.model.User
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ProfileData {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun setProfile(id: String, user: User): Resource<Boolean> =
        suspendCoroutine { cont ->
            db.collection("users").document(id)
                .set(user)
                .addOnSuccessListener {
                    cont.resume(Resource.Success(true))
                }
                .addOnFailureListener {
                    cont.resume(Resource.Error(it))
                }
        }

    @Suppress("UNCHECKED_CAST")
    suspend fun getProfile(id: String): Resource<UserResponse> =
        suspendCoroutine { cont ->
            db.collection("users").document(id)
                .get()
                .addOnSuccessListener {
                    val user = it.toObject<UserResponse>()
                    try {
                        cont.resume(Resource.Success(user) as Resource<UserResponse>)
                    } catch (e: Exception) {
                        cont.resume(Resource.Error(e))
                    }
                }
                .addOnFailureListener {
                    cont.resume(Resource.Error(it))
                }
        }
}