package com.yusril.nutrify.di

import com.yusril.nutrify.core.domain.usecase.NutrifyInteractor
import com.yusril.nutrify.core.domain.usecase.NutrifyUseCase
import com.yusril.nutrify.ui.profile.ProfileViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val useCaseModule = module {
    factory<NutrifyUseCase> { NutrifyInteractor(get()) }
}

val viewModelModule = module {
    viewModel { ProfileViewModel(get()) }
}