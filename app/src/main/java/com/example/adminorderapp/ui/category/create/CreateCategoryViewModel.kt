package com.example.adminorderapp.ui.category.create

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminorderapp.api.ApiResult
import com.example.adminorderapp.data.category.CategoryRepository
import com.example.adminorderapp.ui.UiState
import com.example.adminorderapp.util.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class CreateCategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel(){
    private val _uiState = MutableLiveData(UiState())
    val uiState get() = _uiState
    var name = ""
        private set
    private var part : MultipartBody.Part? = null

    var imageUri = ""
    var canMakeRequest = MutableLiveData(false)
        private set

    fun onNameChange(n : String){
        name = n
        canMakeRequest.value = part != null && name.isNotEmpty()
    }
    fun onImageSelected(p : MultipartBody.Part?){
        part = p
        canMakeRequest.value = part != null && name.isNotEmpty()
    }
    fun messageShown(){
        _uiState.value = UiState()
    }
    fun addItem(){
        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            val body = RequestBody.create(MultipartBody.FORM,name)
            when(val result = categoryRepository.addCategory(body,part!!)){
                is ApiResult.Success -> _uiState.value = UiState(isSuccessful = true)
                is ApiResult.Error -> _uiState.value = UiState(message = result.message)
                is ApiResult.Exception -> _uiState.value = UiState(message = Message.SERVER_BREAKDOWN)
            }
        }
    }

}