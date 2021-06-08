package com.yusril.nutrify.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yusril.nutrify.core.data.Resource
import com.yusril.nutrify.core.domain.model.User
import com.yusril.nutrify.core.domain.usecase.NutrifyUseCase
import kotlinx.coroutines.launch

class ProfileViewModel(private val nutrifyUseCase: NutrifyUseCase) : ViewModel() {

    private val _user = MutableLiveData<Resource<User>>()
    private lateinit var user: LiveData<Resource<User>>
    var id: String = ""

    fun getProfile(): LiveData<Resource<User>> {
        user = _user
        _user.value = Resource.Loading()
        viewModelScope.launch {
            _user.postValue(nutrifyUseCase.getProfile(id))
        }
        return user
    }

    suspend fun setProfile(user: User) {
        nutrifyUseCase.setProfile(id, user)
    }

}