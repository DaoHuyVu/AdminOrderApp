package com.example.adminorderapp.ui.manager

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
class ManagerViewModel @Inject constructor(
    private val staffRepository: StaffRepository
) : ViewModel() {
    private val _managerUiState = MutableLiveData(ManagerUiState())
    init{
        fetchManagerList()
    }
    val managerUiState get() = _managerUiState
    fun getManagerList(){
      _managerUiState.value = ManagerUiState(data = staffRepository.getManagerUiViewList())
    }
    fun fetchManagerList(){
        handleCallback { staffRepository.fetchManagerUiViewList() }
    }
    private fun handleCallback(callback : suspend () -> ApiResult<List<StaffUiView>>){
        _managerUiState.value = _managerUiState.value?.copy(isLoading = true)
        viewModelScope.launch {
            when(val result = callback.invoke()){
                is ApiResult.Success -> _managerUiState.value = ManagerUiState(data = result.data)
                is ApiResult.Error -> _managerUiState.value = _managerUiState.value?.copy(message = result.message, isLoading = false)
                is ApiResult.Exception -> _managerUiState.value = _managerUiState.value?.copy(message = Message.SERVER_BREAKDOWN, isLoading = false)
            }
        }
    }
    fun messageShown(){
        _managerUiState.value = _managerUiState.value?.copy(message = null)
    }
    fun deleteManager(id : Long){
        handleCallback { staffRepository.deleteManager(id)}
    }
}