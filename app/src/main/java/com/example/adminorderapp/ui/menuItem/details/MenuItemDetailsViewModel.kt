package com.example.adminorderapp.ui.menuItem.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.adminorderapp.api.ApiResult
import com.example.adminorderapp.api.category.Category
import com.example.adminorderapp.data.category.CategoryRepository
import com.example.adminorderapp.data.menuItem.MenuItemRepository
import com.example.adminorderapp.ui.UiState
import com.example.adminorderapp.util.Message
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import java.util.LinkedList

class MenuItemDetailsViewModel @AssistedInject constructor(
    @Assisted private val id : Long,
    private val categoryRepository: CategoryRepository,
    private val menuItemRepository: MenuItemRepository
)  : ViewModel(){
    private val _uiState = MutableLiveData(UiState())
    private val _category = MutableLiveData<List<Category>?>()
    init{
        viewModelScope.launch {
            when(val result = categoryRepository.getCategories()){
                is ApiResult.Success -> _category.value = result.data
                is ApiResult.Error -> _uiState.value = _uiState.value?.copy(message = result.message, isLoading = false)
                is ApiResult.Exception -> _uiState.value = _uiState.value?.copy(message = Message.SERVER_BREAKDOWN, isLoading = false)
            }
        }
    }
    private val menuItem = menuItemRepository.getMenuItem(id)
    private val selectedCategory = LinkedList(menuItem.categories)
    val category get() = _category
    val uiState get() = _uiState
    var name = menuItem.name
        private set
    var price = menuItem.price
        private set
    var description = menuItem.description
        private set
    var imageUrl = menuItem.imageUrl
        private set

    fun onNameChange(n : String){
        name = n
    }
    fun onPriceChange(p : String){
        price = p
    }
    fun onDescriptionChange(d : String){
        description = d
    }
    fun onUrlChange(url : String){
        imageUrl = url
    }
    fun onCategorySelected(category: Category){
        selectedCategory.add(category)
    }
    fun onCategoryUnSelected(category: Category){
        selectedCategory.remove(category)
    }
    fun isCategoryChecked(category: Category) : Boolean{
        return selectedCategory.contains(category)
    }
    fun messageShown(){
        _uiState.value = UiState()
    }
    fun updateItem(){
        val gson = Gson()
        val fields = hashMapOf<String,String>()
        if(name != menuItem.name) fields["name"] =  name
        if(price != menuItem.price) fields["price"] = price
        if(description != menuItem.description) fields["description"] = description
        if(imageUrl != menuItem.imageUrl) fields["imageUrl"] = imageUrl
        if(isCategoriesChanged()) fields["categories"] = gson.toJson(selectedCategory)
        val f = gson.toJson(fields)
        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            when(val result = menuItemRepository.updateMenuItem(id,f)){
                is ApiResult.Success -> _uiState.value = UiState(isSuccessful = true)
                is ApiResult.Error -> _uiState.value = UiState(message = result.message)
                is ApiResult.Exception -> _uiState.value = UiState(message = Message.SERVER_BREAKDOWN)
            }
        }
    }
    private fun isCategoriesChanged() : Boolean{
        if(selectedCategory.size != menuItem.categories.size) return true
        selectedCategory.forEach{sc ->
            menuItem.categories.firstOrNull{ c -> c.id == sc.id} ?: return true
        }
        return false
    }
    @AssistedFactory
    interface MenuItemDetailsFactory{
        fun create(id : Long) : MenuItemDetailsViewModel
    }
    companion object{
        fun provideFactory(
            menuItemDetailsFactory: MenuItemDetailsFactory,
            id : Long
        ) = object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("Unchecked_cast")
                return menuItemDetailsFactory.create(id) as T
            }
        }
    }
}