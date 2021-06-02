package com.yusril.nutrify.core.utils

import com.yusril.nutrify.core.data.source.firebase.response.UserResponse
import com.yusril.nutrify.core.domain.model.User

object DataMapper {

    fun mapResponseToDomain(input: UserResponse) =
        User(
            birth_date = input.birth_date,
            height = input.height,
            weight = input.weight,
            gender = input.gender
        )

}