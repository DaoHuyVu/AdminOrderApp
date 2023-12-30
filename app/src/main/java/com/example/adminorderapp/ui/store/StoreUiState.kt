package com.example.adminorderapp.ui.store

import com.example.adminorderapp.util.Message
import com.example.adminorderapp.data.store.StoreUiView

data class StoreUiState(
    val isLoading : Boolean = false,
    val message : Message? = null,
    val data : List<StoreUiView> = listOf()
)