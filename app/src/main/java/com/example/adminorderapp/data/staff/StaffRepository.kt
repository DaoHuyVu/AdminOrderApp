package com.example.adminorderapp.data.staff

import com.example.adminorderapp.util.Message
import com.example.adminorderapp.api.ApiResult
import com.example.adminorderapp.api.dispatchers.IODispatcher
import com.example.adminorderapp.api.staff.Staff
import com.example.adminorderapp.api.staff.StaffService
import com.example.adminorderapp.api.staff.StaffUiView
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import java.util.LinkedList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StaffRepository @Inject constructor(
    private val staffService: StaffService,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) {
    private var managerList = LinkedList<Staff>()
    private var shipperList = LinkedList<Staff>()

    fun getManagerUiViewList() : List<StaffUiView>{
        return managerList.map{ manager -> manager.toStaffUiView()}
    }
    suspend fun fetchManagerUiViewList() : ApiResult<List<StaffUiView>>{
        return withContext(ioDispatcher){
            try{
                val response = staffService.getAllManager()
                if(response.isSuccessful){
                    managerList = LinkedList(response.body()!!)
                    ApiResult.Success(response.body()!!.map { manager -> manager.toStaffUiView()})
                }
                else if(response.code() in 400 until 500) ApiResult.Error(Message.LOAD_ERROR)
                else ApiResult.Error(Message.SERVER_BREAKDOWN)
            }catch(ex : UnknownHostException){
                ApiResult.Error(Message.NO_INTERNET_CONNECTION)
            }
            catch (ex : Exception){
                ApiResult.Exception(ex)
            }
        }
    }
    fun getShipperUiViewList() : List<StaffUiView>{
        return shipperList.map{shipper -> shipper.toStaffUiView()}
    }
    suspend fun fetchShipperUiViewList() : ApiResult<List<StaffUiView>> {
        return withContext(ioDispatcher){
            try{
                val response = staffService.getAllShipper()
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
    suspend fun addStaff(map : Map<String,String>) : ApiResult<Staff>{
        return withContext(ioDispatcher){
            try{
                val response = staffService.addStaff(map)
                if(response.isSuccessful){
                    managerList.add(response.body()!!)
                    ApiResult.Success(response.body()!!)
                }
                else if(response.code() in 400 until 500) ApiResult.Error(Message.LOAD_ERROR)
                else ApiResult.Error(Message.SERVER_BREAKDOWN)
            }catch(ex : UnknownHostException){
                ApiResult.Error(Message.NO_INTERNET_CONNECTION)
            }catch (ex : Exception){
                ApiResult.Exception(ex)
            }
        }
    }
    suspend fun deleteManager(id : Long) : ApiResult<List<StaffUiView>>{
        return withContext(ioDispatcher){
            try{
                val response = staffService.deleteManager(id)
                if(response.isSuccessful){
                    managerList.remove(response.body()!!)
                    ApiResult.Success(managerList.toList().map { staff -> staff.toStaffUiView() })
                }
                else if(response.code() in 400 until 500) ApiResult.Error(Message.LOAD_ERROR)
                else ApiResult.Error(Message.SERVER_BREAKDOWN)
            }
            catch(ex : UnknownHostException){
                ApiResult.Error(Message.NO_INTERNET_CONNECTION)
            }catch (ex : Exception){
                ApiResult.Exception(ex)
            }
        }
    }
    fun getManager(id : Long) = managerList.first{ manager -> manager.id == id}
    fun getShipper(id : Long) = shipperList.first{shipper -> shipper.id == id}
    suspend fun updateStaff(id : Long, fields : String) : ApiResult<Staff>{
        return withContext(ioDispatcher){
            try{
                val response = staffService.updateStaff(id,fields)
                if(response.isSuccessful){
                    val index = managerList.indexOfFirst { m -> m.id == id }
                    managerList[index] = response.body()!!
                    ApiResult.Success(response.body()!!)
                }
                else if(response.code() in 400 until 500) ApiResult.Error(message = Message.LOAD_ERROR)
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
                val response = staffService.deleteShipper(id)
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
}