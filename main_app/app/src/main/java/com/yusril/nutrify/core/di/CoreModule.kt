package com.yusril.nutrify.core.di

import com.yusril.nutrify.core.data.source.NutrifyRepository
import com.yusril.nutrify.core.data.source.firebase.FirebaseDataSource
import com.yusril.nutrify.core.data.source.firebase.profile.ProfileData
import com.yusril.nutrify.core.domain.repository.INutrifyRepository
import org.koin.dsl.module


val firebaseModule = module {
    factory { ProfileData() }
}

val repositoryModule = module {
    single { FirebaseDataSource(get()) }
    single<INutrifyRepository> { NutrifyRepository(get()) }
}