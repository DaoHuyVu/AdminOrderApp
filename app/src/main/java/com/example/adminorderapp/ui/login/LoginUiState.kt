package com.example.adminorderapp.ui.login

import com.example.adminorderapp.util.Message

data class LoginUiState(
    val isLoading : Boolean = false,
    val message : Message? = null,
    val isUserLoggedIn : Boolean = false
)
