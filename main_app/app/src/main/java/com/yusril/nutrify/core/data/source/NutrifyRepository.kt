package com.yusril.nutrify.core.data.source

import com.yusril.nutrify.core.data.Resource
import com.yusril.nutrify.core.data.source.firebase.FirebaseDataSource
import com.yusril.nutrify.core.domain.model.User
import com.yusril.nutrify.core.domain.repository.INutrifyRepository
import com.yusril.nutrify.core.utils.DataMapper

class NutrifyRepository(private val firebaseDataSource: FirebaseDataSource) : INutrifyRepository {

    override suspend fun getProfile(id: String): Resource<User> {
        return when (val response = firebaseDataSource.getProfile(id)) {
            is Resource.Success -> Resource.Success(DataMapper.mapResponseToDomain(response.data))
            is Resource.Error -> Resource.Error(response.exception)
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun setProfile(id: String, user: User): Resource<Boolean> =
        firebaseDataSource.setProfile(id, user)

}