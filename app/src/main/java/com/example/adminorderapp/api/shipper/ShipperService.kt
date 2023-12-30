package com.example.adminorderapp.api.shipper

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

interface ShipperService {
    @GET("/admin/shipper")
    suspend fun getAllShipper() : Response<List<Shipper>>
    @POST("/admin/shipper")
    @FormUrlEncoded
    suspend fun addShipper(@FieldMap map : Map<String,String>) : Response<Shipper>
    @DELETE("/admin/shipper/{id}")
    suspend fun deleteShipper(@Path("id") id : Long) : Response<Shipper>
    @PUT("/admin/shipper/{id}")
    @FormUrlEncoded
    suspend fun updateShipper(@Path("id") id : Long,@FieldMap map : Map<String,String>) : Response<Shipper>
    @PATCH("/admin/shipper/{id}")
    @FormUrlEncoded
    suspend fun updateShipper(@Path("id") id : Long,@Field("fields") fields : String) : Response<Shipper>
}