package com.example.adminorderapp.api.staff

import com.example.adminorderapp.api.store.Address

data class StaffUiView(
    val id : Long,
    val name : String,
    val email : String,
    val address : Address
)
