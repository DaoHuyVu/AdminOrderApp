package com.example.adminorderapp.api.menuItem

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface MenuItemService {
    @GET("/admin/menu")
    suspend fun getAllMenuItem() : Response<List<MenuItem>>
    @POST("/admin/menu")
    @Multipart
    suspend fun addMenuItem(
        @Part("name") name : RequestBody,
        @Part("price") price : RequestBody,
        @Part("description") description : RequestBody,
        @Part image : MultipartBody.Part,
        @Part("categories") categories : RequestBody
    ) : Response<MenuItem>
    @DELETE("/admin/menu/{id}")
    suspend fun deleteMenuItem(@Path("id") id : Long) : Response<MenuItem>
    @PATCH("/admin/menu/{id}")
    @Multipart
    suspend fun updateMenuItem(
        @Path("id") id : Long,
        @Part("fields") fields : RequestBody? = null,
        @Part image : MultipartBody.Part? = null
    ) : Response<MenuItem>
}