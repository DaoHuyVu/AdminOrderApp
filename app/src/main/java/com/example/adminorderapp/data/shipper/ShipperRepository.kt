package com.example.adminorderapp.data.shipper

import com.example.adminorderapp.util.Message
import com.example.adminorderapp.api.ApiResult
import com.example.adminorderapp.api.dispatchers.IODispatcher
import com.example.adminorderapp.api.manager.StaffUiView
import com.example.adminorderapp.api.shipper.Shipper
import com.example.adminorderapp.api.shipper.ShipperService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import java.util.LinkedList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShipperRepository @Inject constructor(
    private val shipperService: ShipperService,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) {
    private var shipperList = LinkedList<Shipper>()
    fun getShipperUiViewList() : List<StaffUiView>{
        return shipperList.map{shipper -> shipper.toStaffUiView()}
    }
    suspend fun fetchShipperUiViewList() : ApiResult<List<StaffUiView>> {
        return withContext(ioDispatcher){
            try{
                val response = shipperService.getAllShipper()
                if(response.isSuccessful){
                    shipperList = LinkedList(response.body()!!)
                    ApiResult.Success(response.body()!!.map { shipper -> shipper.toStaffUiView()})
                }
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
    suspend fun addShipper(fields : Map<String,String>) : ApiResult<Shipper>{
        return withContext(ioDispatcher){
            try{
                val response = shipperService.addShipper(fields)
                if(response.isSuccessful){
                    shipperList.add(response.body()!!)
                    ApiResult.Success(response.body()!!)
                }
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
    suspend fun deleteShipper(id : Long) : ApiResult<List<StaffUiView>>{
        return withContext(ioDispatcher){
            try{
                val response = shipperService.deleteShipper(id)
                if(response.isSuccessful){
                    shipperList.remove(response.body()!!)
                    ApiResult.Success(shipperList.toList().map{shipper -> shipper.toStaffUiView()})
                }
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
    fun getShipper(id : Long) = shipperList.first{shipper -> shipper.id == id}
    suspend fun updateShipper(id : Long,fields : String) : ApiResult<Shipper>{
        return withContext(ioDispatcher){
            try{
                val response = shipperService.updateShipper(id,fields)
                if(response.isSuccessful){
                    val index = shipperList.indexOfFirst { s -> s.id == id }
                    shipperList[index] = response.body()!!
                    ApiResult.Success(response.body()!!)
                }
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