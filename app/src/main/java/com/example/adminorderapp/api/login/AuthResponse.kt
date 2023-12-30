package com.example.adminorderapp.api.login

data class AuthResponse(
    val accessToken : String,
    val role : List<String>
)
