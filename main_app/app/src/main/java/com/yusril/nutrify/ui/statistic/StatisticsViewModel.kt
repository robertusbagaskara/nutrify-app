package com.yusril.nutrify.ui.statistic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yusril.nutrify.core.data.Resource
import com.yusril.nutrify.core.domain.model.CaloryPerDay
import com.yusril.nutrify.core.domain.usecase.NutrifyUseCase
import kotlinx.coroutines.launch

class StatisticsViewModel(private val nutrifyUseCase: NutrifyUseCase) : ViewModel() {

    private val _total = MutableLiveData<Resource<List<CaloryPerDay>>>()
    private lateinit var total: LiveData<Resource<List<CaloryPerDay>>>

    var id: String = ""
    var date: String = ""

    fun getTotalCaloriesPerDay(lowerLimit: Int, upperLimit: Int): LiveData<Resource<List<CaloryPerDay>>> {
        total = _total
        _total.value = Resource.Loading()
        viewModelScope.launch {
            _total.postValue(nutrifyUseCase.getTotalCaloriesPerDay(id, date, lowerLimit, upperLimit))
        }
        return total
    }

    suspend fun setTotalCaloriesPerDay(total_calories: CaloryPerDay) {
        nutrifyUseCase.setTotalCaloriesPerDay(id, date, total_calories)
    }
}