package com.yusril.nutrify.di

import com.yusril.nutrify.core.domain.usecase.NutrifyInteractor
import com.yusril.nutrify.core.domain.usecase.NutrifyUseCase
import com.yusril.nutrify.ui.home.HomeViewModel
import com.yusril.nutrify.ui.profile.ProfileViewModel
import com.yusril.nutrify.ui.recommendation.RecommendationViewModel
import com.yusril.nutrify.ui.statistic.StatisticsViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val useCaseModule = module {
    factory<NutrifyUseCase> { NutrifyInteractor(get()) }
}

val viewModelModule = module {
    viewModel { RecommendationViewModel(get()) }
    viewModel { StatisticsViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { HomeViewModel(get()) }
}