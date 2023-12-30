package com.example.adminorderapp.api.menuItem

import com.example.adminorderapp.api.category.Category
import com.example.adminorderapp.data.menuItem.MenuItemUiView

data class MenuItem (
    val id : Long,
    val name : String,
    val price : String,
    val description : String,
    val imageUrl : String,
    val categories : List<Category>
){
    fun toMenuItemUiView() = MenuItemUiView(id,name,price,imageUrl)
}