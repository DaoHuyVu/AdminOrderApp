package com.example.adminorderapp.api.login

import retrofit2.Response
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthService {
    @POST("/admin/login")
    @FormUrlEncoded
    suspend fun login(@FieldMap fields : Map<String,String>) : Response<AuthResponse>

}