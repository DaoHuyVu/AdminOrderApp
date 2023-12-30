package com.example.adminorderapp.ui.revenue

import android.util.Log
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

@HiltViewModel
class RevenueViewModel @Inject constructor(
    private val revenueRepository: RevenueRepository,
    private val menuItemRepository: MenuItemRepository
) : ViewModel(){
    private var isInitialPhaseSetup = true
    private var isInitialDaySetup = true
    private var isInitialYearSetup = true
    private var isInitialMonthSetup = true
    private val _selectedPhasePositionLiveData = MutableLiveData(0)
    val selectedPhasePositionLiveData : LiveData<Int> get() = _selectedPhasePositionLiveData
    val selectedPhasePosition get() =  selectedPhasePositionLiveData.value!!

    private val _menuItems = MutableLiveData<List<String>?>()

    val menuItems : LiveData<List<String>?> get() = _menuItems

    private val _menuItemRevenue = MutableLiveData<List<Revenue>?>()
    val menuItemRevenue : LiveData<List<Revenue>?> get() = _menuItemRevenue
    var selectedMenuItemPosition = 0
        private set

    fun onMenuItemChange(position : Int){
        selectedMenuItemPosition = position
    }

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

    private val _revenueData = MutableLiveData(RevenueUiState())

    val revenueData : LiveData<RevenueUiState> get() = _revenueData

    fun onPhasePositionChange(position : Int){
        _selectedPhasePositionLiveData.value = position
        if(!isInitialPhaseSetup) getRevenues() else isInitialPhaseSetup = false
    }
    fun onSelectedYearPositionChange(position : Int){
        selectedYearPosition = position
        if(!isInitialYearSetup) getRevenues() else isInitialYearSetup = false
    }
    fun onSelectedMonthPositionChange(position : Int){
        selectedMonthPosition = position
        if(!isInitialMonthSetup) getRevenues() else isInitialMonthSetup = false
    }
    fun onSelectedDayPositionChange(position : Int){
        selectedDayPosition = position
        if(!isInitialDaySetup) getRevenues() else isInitialDaySetup = false
    }
    fun messageShown(){
        _revenueData.value = _revenueData.value?.copy(message = null)
    }
    fun getCategories(){
        _menuItems.value = menuItemRepository.getMenuItemName()
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
        if(menuItems.value!!.isNotEmpty()){
            getRevenuesCategory()
        }
    }
    private fun getRevenuesCategory(){
       when(selectedPhasePosition){
           0 -> handleRevenueCategoryCallback {
               revenueRepository.getRevenues(
                   selectedDayValue,
                   selectedMonthValue,
                   selectedYearValue,
                   _menuItems.value?.get(selectedMenuItemPosition))
           }
           1 -> handleRevenueCategoryCallback {
               revenueRepository.getRevenues(
                   month = selectedMonthValue,
                   year = selectedYearValue,
                   category = _menuItems.value?.get(selectedMenuItemPosition))
           }
           2 -> handleRevenueCategoryCallback {
               revenueRepository.getRevenues(
                   year = selectedYearValue,
                   category = _menuItems.value?.get(selectedMenuItemPosition))
           }
       }
    }
    private fun handleRevenueCallback(
        callback1 : suspend () -> ApiResult<List<Revenue>>
    ) {
        _revenueData.value = _revenueData.value?.copy(isLoading = true)
        viewModelScope.launch {
            when (val result = callback1.invoke()) {
                is ApiResult.Success -> _revenueData.value =
                    _revenueData.value?.copy(overallRevenue = result.data, isLoading = false)
                is ApiResult.Error -> _revenueData.value =
                    _revenueData.value?.copy(message = result.message, isLoading = false)
                is ApiResult.Exception -> _revenueData.value =
                    _revenueData.value?.copy(message = Message.SERVER_BREAKDOWN, isLoading = false)
            }
        }
    }
    private fun handleRevenueCategoryCallback(callback: suspend () -> ApiResult<List<Revenue>>){
        viewModelScope.launch {
            when (val result = callback.invoke()) {
                is ApiResult.Success -> _menuItemRevenue.value = result.data
                is ApiResult.Error -> _revenueData.value =
                    _revenueData.value?.copy(message = result.message, isLoading = false)
                is ApiResult.Exception -> _revenueData.value =
                    _revenueData.value?.copy(message = Message.SERVER_BREAKDOWN, isLoading = false)
            }
        }
    }
}
