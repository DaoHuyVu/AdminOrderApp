package com.example.adminorderapp.ui.menuItem.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminorderapp.api.ApiResult
import com.example.adminorderapp.api.category.Category
import com.example.adminorderapp.data.category.CategoryRepository
import com.example.adminorderapp.data.menuItem.MenuItemRepository
import com.example.adminorderapp.ui.UiState
import com.example.adminorderapp.util.Message
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.LinkedList
import javax.inject.Inject

@HiltViewModel
class CreateMenuItemViewModel @Inject constructor(
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
    private val _canMakeQuery = MutableLiveData(false)
    val canMakeQuery : LiveData<Boolean> get() = _canMakeQuery
    private val selectedCategory = LinkedList<Category>()
    val category get() = _category
    val uiState get() = _uiState
    var name = ""
        private set
    var price = ""
        private set
    var description = ""
        private set
    var imageUri = ""
        private set
    private var part : MultipartBody.Part? = null
    fun onNameChange(n : String){
        name = n
    }
    fun onPriceChange(p : String){
        price = p
    }
    fun onDescriptionChange(d : String){
        description = d
    }
    fun onImageSelected(uri : String,p : MultipartBody.Part){
        imageUri = uri
        part = p
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

    fun addItem(){
        val gson = Gson()
        val nameBody = RequestBody.create(MultipartBody.FORM,name)
        val descriptionBody = RequestBody.create(MultipartBody.FORM,description)
        val priceBody  = RequestBody.create(MultipartBody.FORM,price)
        val categoriesBody = RequestBody.create(MultipartBody.FORM,gson.toJson(selectedCategory))

        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            when(val result = menuItemRepository.addItem(nameBody,priceBody,descriptionBody,part!!,categoriesBody)){
                is ApiResult.Success -> _uiState.value = UiState(isSuccessful = true)
                is ApiResult.Error -> _uiState.value = UiState(message = result.message)
                is ApiResult.Exception -> _uiState.value = UiState(message = Message.SERVER_BREAKDOWN)
            }
        }
    }
}