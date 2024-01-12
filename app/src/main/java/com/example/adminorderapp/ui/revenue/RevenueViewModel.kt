package com.example.adminorderapp.ui.revenue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminorderapp.api.ApiResult
import com.example.adminorderapp.api.revenue.Revenue
import com.example.adminorderapp.data.menuItem.MenuItemRepository
import com.example.adminorderapp.data.revenue.RevenueRepository
import com.example.adminorderapp.util.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import kotlin.system.measureTimeMillis

@HiltViewModel
class RevenueViewModel @Inject constructor(
    private val revenueRepository: RevenueRepository,
    private val menuItemRepository: MenuItemRepository
) : ViewModel(){

    private val _menuItems = MutableLiveData(listOf<String>())
    init{
        viewModelScope.launch {
            _menuItems.value = menuItemRepository.getMenuItemName()
        }
    }
    val menuItems : LiveData<List<String>> get() = _menuItems

    private val _menuItemRevenue = MutableLiveData(listOf<Revenue>())

    val menuItemRevenue : LiveData<List<Revenue>> get() = _menuItemRevenue

    var selectedMenuItemPosition = 0
        private set

    private val _revenueUiState = MutableLiveData(RevenueUiState())

    val revenueUiState : LiveData<RevenueUiState> get() = _revenueUiState

    var from : String? = null
        private set
    var to : String? = null
        private set
    private var canMakeQuery = false

    fun selectedMenuItemChange(position : Int){
        selectedMenuItemPosition = position
        if(canMakeQuery){
            handleMenuItemRevenueCallback { revenueRepository.getRevenues(from!!,to!!,menuItems.value?.get(selectedMenuItemPosition)) }
        }
    }
    fun messageShown(){
        _revenueUiState.value = _revenueUiState.value?.copy(message = null)
    }
    fun fromSelected(date : String){
        from = date
        canMakeQuery = from != null && to != null
        if(canMakeQuery) {
            handleRevenueCallback { revenueRepository.getRevenues(from!!,to!!) }
            handleMenuItemRevenueCallback { revenueRepository.getRevenues(from!!,to!!,menuItems.value?.get(selectedMenuItemPosition)) }
        }
    }
    fun toSelected(date : String){
        to = date
        canMakeQuery = from != null && to != null
        if(canMakeQuery) {
            handleRevenueCallback { revenueRepository.getRevenues(from!!,to!!) }
            handleMenuItemRevenueCallback { revenueRepository.getRevenues(from!!,to!!,menuItems.value?.get(selectedMenuItemPosition)) }
        }
    }

    private fun handleRevenueCallback(
        callback : suspend () -> ApiResult<List<Revenue>>
    ) {
        _revenueUiState.value = _revenueUiState.value?.copy(isLoading = true)
        viewModelScope.launch {
            when (val result = callback.invoke()) {
                is ApiResult.Success -> _revenueUiState.value =
                    _revenueUiState.value?.copy(overallRevenue = result.data, isLoading = false)
                is ApiResult.Error -> _revenueUiState.value =
                    _revenueUiState.value?.copy(message = result.message, isLoading = false)
                is ApiResult.Exception -> _revenueUiState.value =
                    _revenueUiState.value?.copy(message = Message.SERVER_BREAKDOWN, isLoading = false)
            }
        }
    }
    private fun handleMenuItemRevenueCallback(callback: suspend () -> ApiResult<List<Revenue>>){
        viewModelScope.launch {
            when (val result = callback.invoke()) {
                is ApiResult.Success -> _menuItemRevenue?.value = result.data
                is ApiResult.Error -> _revenueUiState.value =
                    _revenueUiState.value?.copy(message = result.message, isLoading = false)
                is ApiResult.Exception -> _revenueUiState.value =
                    _revenueUiState.value?.copy(message = Message.SERVER_BREAKDOWN, isLoading = false)
            }
        }
    }
}
