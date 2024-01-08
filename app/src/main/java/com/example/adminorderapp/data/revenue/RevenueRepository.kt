package com.example.adminorderapp.data.revenue

import com.example.adminorderapp.api.ApiResult
import com.example.adminorderapp.api.dispatchers.IODispatcher
import com.example.adminorderapp.api.revenue.Revenue
import com.example.adminorderapp.api.revenue.RevenueService
import com.example.adminorderapp.util.Message
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RevenueRepository @Inject constructor(
    private val revenueService: RevenueService,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun getRevenues(day : Int? = null,month : Int? = null,year : Int,category : String? = null) : ApiResult<List<Revenue>>{
        return withContext(ioDispatcher){
            try {
                val response = revenueService.getRevenue(day,month,year, category)
                if(response.isSuccessful)
                    ApiResult.Success(response.body()!!)
                else if(response.code() in 400 .. 500 ){
                    ApiResult.Error(Message.LOAD_ERROR)
                }
                else ApiResult.Error(Message.SERVER_BREAKDOWN)
            }catch(ex : UnknownHostException){
                ApiResult.Error(Message.NO_INTERNET_CONNECTION)
            }catch (ex : Exception){
                ApiResult.Exception(ex)
            }
        }
    }
}