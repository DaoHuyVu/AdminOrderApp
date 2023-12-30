package com.example.adminorderapp.api.menuItem

import com.example.adminorderapp.api.category.Category
import com.example.adminorderapp.api.shipper.Shipper
import com.example.adminorderapp.api.store.Store
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MenuItemService {
    @GET("/admin/menu")
    suspend fun getAllMenuItem() : Response<List<MenuItem>>
    @POST("/admin/menu")
    @FormUrlEncoded
    suspend fun addMenuItem(@FieldMap map : Map<String,String>) : Response<MenuItem>
    @DELETE("/admin/menu/{id}")
    suspend fun deleteMenuItem(@Path("id") id : Long) : Response<MenuItem>
    @PUT("/admin/menu/{id}")
    @FormUrlEncoded
    suspend fun updateMenuItem(@Path("id") id : Long,@FieldMap map : Map<String,String>) : Response<MenuItem>
    @PATCH("/admin/menu/{id}")
    @FormUrlEncoded
    suspend fun updateMenuItem(@Path("id") id : Long,@Field("fields") fields : String) : Response<MenuItem>
}