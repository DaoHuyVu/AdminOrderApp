package com.example.adminorderapp.ui.store

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminorderapp.util.Message
import com.example.adminorderapp.api.ApiResult
import com.example.adminorderapp.data.store.StoreRepository
import com.example.adminorderapp.data.store.StoreUiView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val storeRepository: StoreRepository
) : ViewModel(){
    private val _storeUiState = MutableLiveData(StoreUiState())
    init{
        fetchStoreList()
    }
    val storeUiState get() = _storeUiState
    fun getStoreList(){
        _storeUiState.value = StoreUiState(data = storeRepository.getAllStoreUiView())
    }
    private fun handleCallback(callback : suspend () -> ApiResult<List<StoreUiView>>) {
        _storeUiState.value = _storeUiState.value?.copy(isLoading = true)
        viewModelScope.launch {
            when (val result = callback.invoke()) {
                is ApiResult.Success -> _storeUiState.value = StoreUiState(data = result.data)
                is ApiResult.Error -> _storeUiState.value =
                    _storeUiState.value?.copy(message = result.message, isLoading = false)
                is ApiResult.Exception -> _storeUiState.value =
                    _storeUiState.value?.copy(message = Message.SERVER_BREAKDOWN, isLoading = false)
            }
        }
    }
    fun fetchStoreList(){
        handleCallback { storeRepository.fetchStoreUiViewList() }

    }
    fun deleteStore(id : Long){
        handleCallback { storeRepository.deleteStore(id) }
    }
    fun messageShown(){
        _storeUiState.value = _storeUiState.value?.copy(message = null)
    }
}