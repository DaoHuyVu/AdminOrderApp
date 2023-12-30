package com.example.adminorderapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(): ViewModel() {
    private val _uiState = MutableLiveData(ActivityUiState())
    val uiState get() = _uiState
    fun navigateToHome(){
        _uiState.value = ActivityUiState(isShownToolbar = true)
    }
    fun navigateToLogin(){
        _uiState.value = ActivityUiState()
    }
}