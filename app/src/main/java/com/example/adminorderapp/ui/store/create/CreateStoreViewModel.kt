package com.example.adminorderapp.ui.store.create

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminorderapp.api.ApiResult
import com.example.adminorderapp.data.store.StoreRepository
import com.example.adminorderapp.ui.UiState
import com.example.adminorderapp.util.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class CreateStoreViewModel @Inject constructor(
    private val storeRepository: StoreRepository
) :ViewModel() {
    private val _uiState = MutableLiveData<UiState>()
    val uiState get() = _uiState
    var city = ""
    var district = ""
    var street = ""
    var openingTime = ""
    var closingTime = ""
    fun addStore(){
        val fields = hashMapOf<String,String>()
        fields["city"] = city
        fields["district"] = district
        fields["street"] = street
        fields["openingTime"] = openingTime
        fields["closingTime"] = closingTime
        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            when(val result = storeRepository.addStore(fields)){
                is ApiResult.Success -> _uiState.value = UiState(isSuccessful = true)
                is ApiResult.Error -> _uiState.value = UiState(message = result.message)
                is ApiResult.Exception -> _uiState.value = UiState(message = Message.SERVER_BREAKDOWN)
            }
        }
    }
    fun messageShown(){
        _uiState.value = UiState()
    }
}