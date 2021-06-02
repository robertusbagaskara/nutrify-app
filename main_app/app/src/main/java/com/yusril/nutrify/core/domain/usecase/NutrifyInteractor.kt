package com.yusril.nutrify.core.domain.usecase

import com.yusril.nutrify.core.data.Resource
import com.yusril.nutrify.core.domain.model.User
import com.yusril.nutrify.core.domain.repository.INutrifyRepository

class NutrifyInteractor(private val iNutrifyRepository: INutrifyRepository) : NutrifyUseCase {

    override suspend fun getProfile(id: String): Resource<User> =
        iNutrifyRepository.getProfile(id)

    override suspend fun setProfile(id: String, user: User): Resource<Boolean> =
        iNutrifyRepository.setProfile(id, user)

}