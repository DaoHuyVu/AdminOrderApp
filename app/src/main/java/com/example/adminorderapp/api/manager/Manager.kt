package com.example.adminorderapp.api.manager

import com.example.adminorderapp.api.store.Address


data class Manager(
    val id : Long,
    val name : String,
    val email : String,
    val dateOfBirth : String,
    val salary : String,
    val gender : String,
    val phone : String,
    val address : Address
){
    fun toStaffUiView() : StaffUiView = StaffUiView(id,name,email,address)
}
