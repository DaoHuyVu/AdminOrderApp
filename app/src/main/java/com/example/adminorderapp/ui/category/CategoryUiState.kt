package com.example.adminorderapp.ui.category

import com.example.adminorderapp.api.category.Category
import com.example.adminorderapp.util.Message

data class CategoryUiState (
    val isLoading : Boolean = false,
    val message : Message? = null,
    val data : List<Category> = listOf()
)