package com.example.adminorderapp.ui.store.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.adminorderapp.api.ApiResult
import com.example.adminorderapp.data.store.StoreRepository
import com.example.adminorderapp.ui.UiState
import com.example.adminorderapp.util.Message
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class StoreDetailsViewModel @AssistedInject constructor(
    @Assisted private val id : Long,
    private val storeRepository: StoreRepository
) : ViewModel() {
    private val _uiState = MutableLiveData<UiState>()
    val uiState get() = _uiState
    private val store = storeRepository.getStore(id)
    var city = store.address.city
    var district = store.address.district
    var street = store.address.street
    var openingTime = store.openingTime
    var closingTime = store.closingTime
    private fun isCityChanged() = city != store.address.city
    private fun isDistrictChanged() = district != store.address.district
    private fun isStreetChanged() = street != store.address.street
    private fun isOpeningTimeChanged() = openingTime != store.openingTime
    private fun isClosingTimeChanged() = closingTime != store.closingTime
    fun updateStore(){
        val fields = hashMapOf<String,String>()
        if(isCityChanged()) fields["city"] = city
        if(isDistrictChanged()) fields["district"] = district
        if(isStreetChanged()) fields["street"] = street
        if(isOpeningTimeChanged()) fields["openingTime"] = openingTime
        if(isClosingTimeChanged()) fields["closingTime"] = closingTime
        val gson = Gson()
        val fieldsStr = gson.toJson(fields)
        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            when(val result = storeRepository.updateStore(id,fieldsStr)){
                is ApiResult.Success -> _uiState.value = UiState(isSuccessful = true)
                is ApiResult.Error -> _uiState.value = UiState(message = result.message)
                is ApiResult.Exception -> _uiState.value = UiState(message = Message.SERVER_BREAKDOWN)
            }
        }
    }

    fun messageShown(){
        _uiState.value = UiState()
    }
    @AssistedFactory
    interface StoreDetailsFactory{
        fun create(id : Long) : StoreDetailsViewModel
    }
    companion object{
        fun provideFactory(
            storeDetailsFactory: StoreDetailsFactory,
            id : Long
        ) = object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("Unchecked_cast")
                return storeDetailsFactory.create(id) as T
            }
        }
    }
}