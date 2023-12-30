package com.example.adminorderapp.ui.menuItem

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.adminorderapp.util.Message
import com.example.adminorderapp.api.ApiResult
import com.example.adminorderapp.api.category.Category
import com.example.adminorderapp.data.category.CategoryRepository
import com.example.adminorderapp.data.menuItem.MenuItemRepository
import com.example.adminorderapp.data.menuItem.MenuItemUiView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuItemViewModel @Inject constructor(
    private val menuItemRepository: MenuItemRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _menuItemUiState = MutableLiveData(MenuItemUiState())
    init{
        fetchMenuItemList()
    }
    val menuItemUiState get() = _menuItemUiState
    private val _categories = MutableLiveData<List<Category>?>()
    init{
        getCategories()
    }
    val categories : LiveData<Array<String>?> = _categories.map { list ->
        list?.map{it.name}?.toTypedArray()
    }
    // Display all menu item by default
    var selectedCategory : Category? = null
        private set
    var position = 0
        private set
    fun onChangeCategory(categoryId : Int){
        // categoryId - 1 since the first item in spinner is null
        position = categoryId
        selectedCategory = if(categoryId >= 1) _categories.value?.get(categoryId-1) else null
        getMenuItemList(selectedCategory)
    }
    private fun getCategories(){
        viewModelScope.launch {
            _menuItemUiState.value = _menuItemUiState.value?.copy(isLoading = true)
            viewModelScope.launch {
                when(val result = categoryRepository.getCategories()){
                    is ApiResult.Success -> _categories.value = result.data
                    is ApiResult.Error -> _menuItemUiState.value = _menuItemUiState.value?.copy(message = result.message, isLoading = false)
                    is ApiResult.Exception -> _menuItemUiState.value = _menuItemUiState.value?.copy(message = Message.SERVER_BREAKDOWN, isLoading = false)
                }
            }
        }
    }
    fun getMenuItemList(category : Category?){
        _menuItemUiState.value = MenuItemUiState(data = menuItemRepository.getMenuItemUiViewList(category))
    }
    private fun handleCallback(callback : suspend () -> ApiResult<List<MenuItemUiView>>){
        _menuItemUiState.value = _menuItemUiState.value?.copy(isLoading = true)
        viewModelScope.launch {
            when(val result = callback.invoke()){
                is ApiResult.Success -> _menuItemUiState.value = MenuItemUiState(data = result.data)
                is ApiResult.Error -> _menuItemUiState.value = _menuItemUiState.value?.copy(message = result.message, isLoading = false)
                is ApiResult.Exception -> _menuItemUiState.value = _menuItemUiState.value?.copy(message = Message.SERVER_BREAKDOWN, isLoading = false)
            }
        }
    }

    fun fetchMenuItemList(){
        getCategories()
        handleCallback { menuItemRepository.fetchMenuItemUiViewList() }
    }
    fun deleteMenuItem(id : Long){
        handleCallback {
            menuItemRepository.deleteMenuItem(id)
        }
    }
    fun messageShown(){
        _menuItemUiState.value = _menuItemUiState.value?.copy(message = null)
    }
}