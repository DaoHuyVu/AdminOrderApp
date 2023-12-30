package com.example.adminorderapp.api.revenue

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RevenueService {
    @GET("admin/revenue")
    suspend fun getRevenue(
        @Query("day") day : Int? = null,
        @Query("month") month : Int? = null,
        @Query("year") year : Int,
        @Query("category") category : String? = null
    ) : Response<List<Revenue>>
}