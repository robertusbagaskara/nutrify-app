package com.yusril.nutrify.core.data

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error<out T>(val exception: String?) : Resource<T>()
    class Loading<out T> : Resource<T>()
    companion object {
        lateinit var Error: Any
        lateinit var Success: Any
        lateinit var Loading: Any
    }
}