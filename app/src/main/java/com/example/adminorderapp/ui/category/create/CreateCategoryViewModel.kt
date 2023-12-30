package com.example.adminorderapp.ui.category.create

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminorderapp.api.ApiResult
import com.example.adminorderapp.data.category.CategoryRepository
import com.example.adminorderapp.ui.UiState
import com.example.adminorderapp.util.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateCategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel(){
    private val _uiState = MutableLiveData(UiState())
    val uiState get() = _uiState
    var name = ""
        private set
    var imageUrl = ""
        private set

    fun onNameChange(n : String){
        name = n
    }
    fun onUrlChange(url : String){
        imageUrl = url
    }
    fun messageShown(){
        _uiState.value = UiState()
    }
    fun addItem(){
        val fields = hashMapOf<String,String>()
        fields["name"] =  name
        fields["imageUrl"] = imageUrl
        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            when(val result = categoryRepository.addCategory(fields)){
                is ApiResult.Success -> _uiState.value = UiState(isSuccessful = true)
                is ApiResult.Error -> _uiState.value = UiState(message = result.message)
                is ApiResult.Exception -> _uiState.value = UiState(message = Message.SERVER_BREAKDOWN)
            }
        }
    }
}