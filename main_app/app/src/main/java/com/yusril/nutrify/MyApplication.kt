package com.yusril.nutrify

import android.app.Application
import com.yusril.nutrify.core.di.profileModule
import com.yusril.nutrify.core.di.recommendationModule
import com.yusril.nutrify.core.di.repositoryModule
import com.yusril.nutrify.core.di.statisticModule
import com.yusril.nutrify.di.useCaseModule
import com.yusril.nutrify.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@MyApplication)
            modules(
                listOf(
                    profileModule,
                    statisticModule,
                    recommendationModule,
                    repositoryModule,
                    useCaseModule,
                    viewModelModule
                )
            )
        }
    }

}