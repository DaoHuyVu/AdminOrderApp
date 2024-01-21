package com.example.adminorderapp.ui.shipper

import com.example.adminorderapp.util.Message
import com.example.adminorderapp.api.staff.StaffUiView

data class ShipperUiState(
    val isLoading : Boolean = false,
    val message : Message? = null,
    val data : List<StaffUiView> = listOf()
)
