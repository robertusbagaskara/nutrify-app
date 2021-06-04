package com.yusril.nutrify.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import com.yusril.nutrify.core.data.Resource
import com.yusril.nutrify.core.domain.model.ListStatistics
import com.yusril.nutrify.core.domain.usecase.NutrifyUseCase
import kotlinx.coroutines.launch

class HomeViewModel(private val nutrifyUseCase: NutrifyUseCase) : ViewModel() {

    private val _stats = MutableLiveData<Resource<List<ListStatistics>>>()
    private lateinit var stats: LiveData<Resource<List<ListStatistics>>>
    var id: String = ""
    var date: String = ""
    var time: String = ""

    fun getStatisticToday(): LiveData<Resource<List<ListStatistics>>> {
        stats = _stats
        _stats.value = Resource.Loading()
        viewModelScope.launch {
            _stats.postValue(nutrifyUseCase.getStatisticToday(id, date))
        }
        return stats
    }

    suspend fun setStatistic(listStatistics: ListStatistics) {
        nutrifyUseCase.setStatistic(id, date, time, listStatistics)
    }
}