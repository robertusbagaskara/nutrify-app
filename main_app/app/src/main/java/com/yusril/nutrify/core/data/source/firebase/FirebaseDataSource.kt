package com.yusril.nutrify.core.data.source.firebase

import com.yusril.nutrify.core.data.Resource
import com.yusril.nutrify.core.data.source.firebase.profile.ProfileData
import com.yusril.nutrify.core.data.source.firebase.response.FirebaseResponse
import com.yusril.nutrify.core.data.source.firebase.response.UserResponse
import com.yusril.nutrify.core.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FirebaseDataSource(private val profileData: ProfileData) {

    suspend fun getProfile(id: String): Resource<UserResponse> =
        profileData.getProfile(id)

    suspend fun setProfile(id: String, user: User) =
        profileData.setProfile(id, user)

}