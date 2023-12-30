package com.example.adminorderapp.ui.category

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminorderapp.api.ApiResult
import com.example.adminorderapp.api.category.Category
import com.example.adminorderapp.data.category.CategoryRepository
import com.example.adminorderapp.util.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private val _categoryUiState = MutableLiveData(CategoryUiState())
    init{
        fetchCategoryList()
    }
    val categoryUiState get() = _categoryUiState
    fun getCategoryList(){
        handleCallback { categoryRepository.getCategories()}
    }
    fun fetchCategoryList() {
        handleCallback { categoryRepository.fetchCategory()}
    }
    private fun handleCallback(callback : suspend () -> ApiResult<List<Category>>) {
        _categoryUiState.value = _categoryUiState.value?.copy(isLoading = true)
        viewModelScope.launch {
            when (val result = callback.invoke()) {
                is ApiResult.Success -> _categoryUiState.value = CategoryUiState(data = result.data)
                is ApiResult.Error -> _categoryUiState.value =
                    _categoryUiState.value?.copy(message = result.message, isLoading = false)

                is ApiResult.Exception -> _categoryUiState.value =
                    _categoryUiState.value?.copy(message = Message.SERVER_BREAKDOWN, isLoading = false)
            }
        }
    }
    fun deleteCategory(id : Long){
        handleCallback { categoryRepository.deleteCategory(id) }
    }
    fun messageShown(){
        _categoryUiState.value = _categoryUiState.value?.copy(message = null)
    }
}