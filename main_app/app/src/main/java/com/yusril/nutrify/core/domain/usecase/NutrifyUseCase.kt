package com.yusril.nutrify.core.domain.usecase

import com.yusril.nutrify.core.data.Resource
import com.yusril.nutrify.core.domain.model.User

interface NutrifyUseCase {

    suspend fun getProfile(id: String): Resource<User>

    suspend fun setProfile(id: String, user: User): Resource<Boolean>

}