package com.example.adminorderapp.data.store

import com.example.adminorderapp.api.store.Address

data class StoreUiView(
    val id : Long,
    val address : Address
){
    override fun toString(): String {
        return address.toString()
    }
}