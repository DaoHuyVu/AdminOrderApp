package com.example.adminorderapp.ui.revenue

import com.example.adminorderapp.api.category.Category
import com.example.adminorderapp.api.revenue.Revenue
import com.example.adminorderapp.util.Message

data class RevenueUiState(
    val isLoading : Boolean = false,
    val message : Message? = null,
    val overallRevenue : List<Revenue> = listOf(),
)
