package com.yusril.nutrify.core.domain.repository

import com.yusril.nutrify.core.data.Resource
import com.yusril.nutrify.core.data.source.firebase.response.UserResponse
import com.yusril.nutrify.core.domain.model.User

interface INutrifyRepository {

    suspend fun getProfile(id: String): Resource<User>

    suspend fun setProfile(id: String, user: User): Resource<Boolean>

}