package com.example.adminorderapp.ui.category.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.adminorderapp.api.ApiResult
import com.example.adminorderapp.data.category.CategoryRepository
import com.example.adminorderapp.ui.UiState
import com.example.adminorderapp.util.Message
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part
import okhttp3.RequestBody

class CategoryDetailsViewModel @AssistedInject constructor(
    @Assisted private val id : Long,
    private val categoryRepository: CategoryRepository
) : ViewModel(){
    private val _uiState = MutableLiveData(UiState())
    private val category = categoryRepository.getCategory(id)
    val uiState get() = _uiState
    var name = category.name
        private set
    var imageUrl = category.imageUrl
        private set
    private var part : Part? = null
    fun onNameChange(n : String){
        name = n
    }
    fun onImageChange(url : String,p: Part){
        imageUrl = url
        part = p
    }

    fun messageShown(){
        _uiState.value = UiState()
    }
    fun updateItem(){
        val body = RequestBody.create(MultipartBody.FORM,name)
        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            when(val result = categoryRepository.updateCategory(id,body,part)){
                is ApiResult.Success -> _uiState.value = UiState(isSuccessful = true)
                is ApiResult.Error -> _uiState.value = UiState(message = result.message)
                is ApiResult.Exception -> _uiState.value = UiState(message = Message.SERVER_BREAKDOWN)
            }
        }
    }
    @AssistedFactory
    interface CategoryDetailsFactory{
        fun create(id : Long) : CategoryDetailsViewModel
    }
    companion object{
        fun provideFactory(
            categoryDetailsFactory: CategoryDetailsFactory,
            id : Long
        ) = object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("Unchecked_cast")
                return categoryDetailsFactory.create(id) as T
            }
        }
    }
}