package com.example.adminorderapp.api.staff

import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface StaffService {
    @GET("/admin/manager")
    suspend fun getAllManager() : Response<List<Staff>>
    @POST("/admin/staff")
    @FormUrlEncoded
    suspend fun addStaff(@FieldMap map : Map<String,String>) : Response<Staff>
    @DELETE("admin/manager/{id}")
    suspend fun deleteManager(@Path("id") id : Long) : Response<Staff>
    @PATCH("/admin/manager/{id}")
    @FormUrlEncoded
    suspend fun updateStaff(@Path("id") id : Long, @Field("fields") fields : String ) : Response<Staff>
    @GET("/admin/shipper")
    suspend fun getAllShipper() : Response<List<Staff>>
    @DELETE("/admin/shipper/{id}")
    suspend fun deleteShipper(@Path("id") id : Long) : Response<Staff>
}