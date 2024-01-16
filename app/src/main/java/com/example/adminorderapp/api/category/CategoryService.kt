package com.example.adminorderapp.api.category

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
interface CategoryService {
    @GET("/api/category")
    suspend fun getAllCategory() : Response<List<Category>>
    @Multipart
    @POST("/admin/category")
    suspend fun addCategory(@Part("name") name : RequestBody, @Part image : MultipartBody.Part) : Response<Category>
    @DELETE("admin/category/{id}")
    suspend fun deleteCategory(@Path("id") id : Long) : Response<Category>
    @PATCH("admin/category/{id}")
    @Multipart
    suspend fun updateCategory(
        @Path("id") id : Long,
        @Part("categoryName") categoryName : RequestBody? = null,
        @Part file : MultipartBody.Part? = null
    ) : Response<Category>
}