package com.example.adminorderapp.ui.menuItem

import com.example.adminorderapp.util.Message
import com.example.adminorderapp.data.menuItem.MenuItemUiView

data class MenuItemUiState(
    val isLoading : Boolean = false,
    val message : Message? = null,
    val data : List<MenuItemUiView> = listOf()
)