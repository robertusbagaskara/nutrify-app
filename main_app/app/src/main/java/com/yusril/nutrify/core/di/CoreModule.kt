package com.yusril.nutrify.core.di

import com.yusril.nutrify.core.data.source.NutrifyRepository
import com.yusril.nutrify.core.data.source.firebase.FirebaseDataSource
import com.yusril.nutrify.core.data.source.firebase.profile.ProfileData
import com.yusril.nutrify.core.data.source.firebase.recommendation.RecommendationData
import com.yusril.nutrify.core.data.source.firebase.statistics.StatisticData
import com.yusril.nutrify.core.domain.repository.INutrifyRepository
import org.koin.dsl.module


val profileModule = module {
    factory { ProfileData() }
}

val statisticModule = module {
    factory { StatisticData() }
}

val recommendationModule = module {
    factory { RecommendationData() }
}

val repositoryModule = module {
    single { FirebaseDataSource(get(), get(), get()) }
    single<INutrifyRepository> { NutrifyRepository(get()) }
}