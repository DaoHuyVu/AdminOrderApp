package com.example.adminorderapp.ui.manager

import com.example.adminorderapp.util.Message
import com.example.adminorderapp.api.manager.StaffUiView

data class ManagerUiState(
    val isLoading : Boolean = false,
    val message : Message? = null,
    val data : List<StaffUiView> = listOf()
)
