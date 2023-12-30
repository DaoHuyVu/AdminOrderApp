package com.example.adminorderapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(): ViewModel()  {
    private val _isShownFab = MutableLiveData(true)
    val isShownFab get() = _isShownFab

    fun navigateToRevenue(){
        _isShownFab.value = false
    }
    fun navigateToOther(){
        _isShownFab.value = true
    }
}