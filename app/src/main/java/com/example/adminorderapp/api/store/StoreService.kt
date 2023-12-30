package com.example.adminorderapp.api.store

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

interface StoreService {
    @GET("/admin/store")
    suspend fun getAllStore(@Query("isManaged") isManaged : Boolean? = null) : Response<List<Store>>
    @POST("/admin/store")
    @FormUrlEncoded
    suspend fun addStore(@FieldMap map : Map<String,String>) : Response<Store>
    @DELETE("/admin/store/{id}")
    suspend fun deleteStore(@Path("id") id : Long) : Response<Store>
    @PUT("/admin/store/{id}")
    @FormUrlEncoded
    suspend fun updateStore(@Path("id") id : Long,@FieldMap map : Map<String,String>) : Response<Store>
    @PATCH("/admin/store/{id}")
    @FormUrlEncoded
    suspend fun updateStore(@Path("id") id : Long,@Field("fields") fields : String) : Response<Store>
}