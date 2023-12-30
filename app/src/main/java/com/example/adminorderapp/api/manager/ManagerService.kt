package com.example.adminorderapp.api.manager

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

interface ManagerService {
    @GET("/admin/manager")
    suspend fun getAllManager() : Response<List<Manager>>
    @POST("/admin/manager")
    @FormUrlEncoded
    suspend fun addManager(@FieldMap map : Map<String,String>) : Response<Manager>
    @DELETE("admin/manager/{id}")
    suspend fun deleteManager(@Path("id") id : Long) : Response<Manager>
    @PUT("/admin/manager/{id}")
    @FormUrlEncoded
    suspend fun updateManager(@Path("id") id : Long,@FieldMap map : Map<String,String>) : Response<Manager>
    @PATCH("/admin/manager/{id}")
    @FormUrlEncoded
    suspend fun updateManager(@Path("id") id : Long,@Field("fields") fields : String ) : Response<Manager>
}