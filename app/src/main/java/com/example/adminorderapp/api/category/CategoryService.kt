package com.example.adminorderapp.api.category

import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
interface CategoryService {
    @GET("/api/category")
    suspend fun getAllCategory() : Response<List<Category>>
    @FormUrlEncoded
    @POST("/admin/category")
    suspend fun addCategory(@FieldMap fields : Map<String,String>) : Response<Category>
    @DELETE("admin/category/{id}")
    suspend fun deleteCategory(@Path("id") id : Long) : Response<Category>
    @PUT("admin/category/{id}")
    @FormUrlEncoded
    suspend fun updateCategory(@Path("id") id : Long,@FieldMap fields : Map<String,String>) : Response<Category>
    @PATCH("admin/category/{id}")
    @FormUrlEncoded
    suspend fun updateCategory(@Path("id") id : Long,@Field("fields") fields : String) : Response<Category>
}