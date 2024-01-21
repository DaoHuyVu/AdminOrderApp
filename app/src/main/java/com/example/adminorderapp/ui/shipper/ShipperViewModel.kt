package com.example.adminorderapp.ui.shipper

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminorderapp.util.Message
import com.example.adminorderapp.api.ApiResult
import com.example.adminorderapp.api.staff.StaffUiView
import com.example.adminorderapp.data.staff.StaffRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShipperViewModel @Inject constructor(
    private val staffRepository: StaffRepository
) : ViewModel(){
    private val _shipperUiState = MutableLiveData(ShipperUiState())
    init{
        fetchShipperList()
    }
    val managerUiState get() = _shipperUiState
    fun getShipperList(){
        _shipperUiState.value = ShipperUiState(data = staffRepository.getShipperUiViewList())
    }
    private fun handleCallback(callback : suspend () -> ApiResult<List<StaffUiView>>){
        _shipperUiState.value = _shipperUiState.value?.copy(isLoading = true)
        viewModelScope.launch {
            when(val result = callback.invoke()){
                is ApiResult.Success -> _shipperUiState.value = ShipperUiState(data = result.data)
                is ApiResult.Error -> _shipperUiState.value = _shipperUiState.value?.copy(message = result.message, isLoading = false)
                is ApiResult.Exception -> _shipperUiState.value = _shipperUiState.value?.copy(message = Message.SERVER_BREAKDOWN, isLoading = false)
            }
        }
    }
    fun fetchShipperList(){
        handleCallback { staffRepository.fetchShipperUiViewList() }
    }
    fun deleteShipper(id : Long){
        handleCallback { staffRepository.deleteShipper(id) }
    }
    fun messageShown(){
        _shipperUiState.value = _shipperUiState.value?.copy(message = null)
    }
}