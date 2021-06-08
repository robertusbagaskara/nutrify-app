package com.yusril.nutrify.ui.recommendation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yusril.nutrify.core.data.Resource
import com.yusril.nutrify.core.domain.model.RecommendationList
import com.yusril.nutrify.core.domain.usecase.NutrifyUseCase
import kotlinx.coroutines.launch

class RecommendationViewModel(private val nutrifyUseCase: NutrifyUseCase) : ViewModel() {

    private val _list = MutableLiveData<Resource<List<RecommendationList>>>()
    private lateinit var list: LiveData<Resource<List<RecommendationList>>>

    var id: String = ""
    var date: String = ""

    fun getRecommendation(minusCalories: Int): LiveData<Resource<List<RecommendationList>>> {
        list = _list
        _list.value = Resource.Loading()
        viewModelScope.launch {
            _list.postValue(nutrifyUseCase.getRecommendation(minusCalories))
        }
        return list
    }
}