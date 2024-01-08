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

    private val _selectedPhasePositionLiveData = MutableLiveData(0)
    val selectedPhasePositionLiveData : LiveData<Int> get() = _selectedPhasePositionLiveData
    val selectedPhasePosition get() =  selectedPhasePositionLiveData.value!!

    private val _menuItems = MutableLiveData(listOf<String>())

    val menuItems : LiveData<List<String>> get() = _menuItems

    private val _menuItemRevenue = MutableLiveData(listOf<Revenue>())
    val menuItemRevenue : LiveData<List<Revenue>> get() = _menuItemRevenue
    var selectedMenuItemPosition = 0
        private set
    private val _revenueUiState = MutableLiveData(RevenueUiState())

    val revenueUiState : LiveData<RevenueUiState> get() = _revenueUiState

    private val calendar = Calendar.getInstance()

    var selectedYearPosition = 0
        private set
    private val selectedYearValue get() =  calendar.get(Calendar.YEAR) - selectedYearPosition

    var selectedMonthPosition = calendar.get(Calendar.MONTH)
        private set
    private val selectedMonthValue get() =  selectedMonthPosition + 1

    var selectedDayPosition = calendar.get(Calendar.DAY_OF_MONTH) - 1
        private set
    private val selectedDayValue get() =  selectedDayPosition + 1

    fun initialCall(){
        viewModelScope.launch {
            _menuItems.value = menuItemRepository.getMenuItemName()
            getRevenues()
        }
    }
    fun onMenuItemChange(position : Int){
        selectedMenuItemPosition = position
        getMenuItemRevenues()
    }
    fun onPhasePositionChange(position : Int){
        _selectedPhasePositionLiveData.value = position
        getRevenues()
    }
    fun onSelectedYearPositionChange(position : Int){
        selectedYearPosition = position
        getRevenues()
    }
    fun onSelectedMonthPositionChange(position : Int){
        selectedMonthPosition = position
        getRevenues()
    }
    fun onSelectedDayPositionChange(position : Int){
        selectedDayPosition = position
        getRevenues()
    }
    fun messageShown(){
        _revenueUiState.value = _revenueUiState.value?.copy(message = null)
    }
    fun getMenuItems(){

    }
    fun getRevenues(){
        when(selectedPhasePosition){
            0 -> handleRevenueCallback{
                    revenueRepository.getRevenues(
                        selectedDayValue,
                        selectedMonthValue,
                        selectedYearValue)
            }
            1 -> handleRevenueCallback{
                    revenueRepository.getRevenues(
                        month = selectedMonthValue,
                        year = selectedYearValue)
                }
            2 -> handleRevenueCallback{ revenueRepository.getRevenues(year = selectedYearValue) }
        }
        if(menuItems.value?.isNotEmpty() == true){
            getMenuItemRevenues()
        }
    }
    private fun getMenuItemRevenues(){
       when(selectedPhasePosition){
           0 -> handleMenuItemRevenueCallback {
               revenueRepository.getRevenues(
                   selectedDayValue,
                   selectedMonthValue,
                   selectedYearValue,
                   _menuItems.value?.get(selectedMenuItemPosition))
           }
           1 -> handleMenuItemRevenueCallback {
               revenueRepository.getRevenues(
                   month = selectedMonthValue,
                   year = selectedYearValue,
                   category = _menuItems.value?.get(selectedMenuItemPosition))
           }
           2 -> handleMenuItemRevenueCallback {
               revenueRepository.getRevenues(
                   year = selectedYearValue,
                   category = _menuItems.value?.get(selectedMenuItemPosition))
           }
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
