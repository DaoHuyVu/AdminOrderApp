package com.example.adminorderapp.api.store

import com.example.adminorderapp.data.store.StoreUiView

data class Store(
    val id : Long,
    val openingTime : String,
    val closingTime : String,
    val address : Address
){
    override fun toString(): String {
        return address.toString()
    }
    fun toStoreUiView() = StoreUiView(id,address)
}
data class Address(
    val city : String,
    val district : String,
    val street : String
){
    override fun toString(): String {
        return "$street - $district - $city"
    }
}

