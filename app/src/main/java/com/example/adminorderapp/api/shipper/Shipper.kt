package com.example.adminorderapp.api.shipper

import com.example.adminorderapp.api.manager.StaffUiView
import com.example.adminorderapp.api.store.Address

data class Shipper(
    val id : Long,
    val name : String,
    val email : String,
    val dateOfBirth : String,
    val salary : String,
    val gender : String,
    val address : Address,
    val phone : String
){
    fun toStaffUiView() = StaffUiView(id,name,email,address)
}
