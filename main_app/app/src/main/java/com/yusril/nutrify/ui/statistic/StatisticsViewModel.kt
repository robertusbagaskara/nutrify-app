package com.yusril.nutrify.ui.statistic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yusril.nutrify.core.data.Resource
import com.yusril.nutrify.core.domain.model.CaloryPerDay
import com.yusril.nutrify.core.domain.model.ListStatistics
import com.yusril.nutrify.core.domain.usecase.NutrifyUseCase
import kotlinx.coroutines.launch

class StatisticsViewModel(private val nutrifyUseCase: NutrifyUseCase) : ViewModel() {

    private val _total = MutableLiveData<Resource<List<CaloryPerDay>>>()
    private lateinit var total: LiveData<Resource<List<CaloryPerDay>>>

    private val _food = MutableLiveData<Resource<List<ListStatistics>>>()
    private lateinit var food: LiveData<Resource<List<ListStatistics>>>

    var id: String = ""
    var date: String = ""

    fun getTotalCaloriesPerDay(lowerLimit: Int, upperLimit: Int): LiveData<Resource<List<CaloryPerDay>>> {
        total = _total
        _total.value = Resource.Loading()
        viewModelScope.launch {
            _total.postValue(nutrifyUseCase.getTotalCaloriesPerDay(id, lowerLimit, upperLimit))
        }
        return total
    }

    fun getStatisticFood(lowerLimit: Int, upperLimit: Int): LiveData<Resource<List<ListStatistics>>> {
        food = _food
        _food.value = Resource.Loading()
        viewModelScope.launch {
            _food.postValue(nutrifyUseCase.getStatisticFood(id, lowerLimit, upperLimit))
        }
        return food
    }

    suspend fun setTotalCaloriesPerDay(total_calories: CaloryPerDay) {
        nutrifyUseCase.setTotalCaloriesPerDay(id, date, total_calories)
    }
}