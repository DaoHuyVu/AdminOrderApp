package com.example.adminorderapp.ui

import com.example.adminorderapp.util.Message

data class UiState(
    val isLoading : Boolean = false,
    val message : Message? = null,
    val isSuccessful : Boolean = false
)
