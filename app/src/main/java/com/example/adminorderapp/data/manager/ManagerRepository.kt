package com.example.adminorderapp.data.manager

import com.example.adminorderapp.util.Message
import com.example.adminorderapp.api.ApiResult
import com.example.adminorderapp.api.dispatchers.IODispatcher
import com.example.adminorderapp.api.manager.Manager
import com.example.adminorderapp.api.manager.ManagerService
import com.example.adminorderapp.api.manager.StaffUiView
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import java.util.LinkedList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ManagerRepository @Inject constructor(
    private val managerService: ManagerService,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) {
    private var managerList = LinkedList<Manager>()

    fun getManagerUiViewList() : List<StaffUiView>{
        return managerList.map{manager -> manager.toStaffUiView()}
    }
    suspend fun fetchManagerUiViewList() : ApiResult<List<StaffUiView>>{
        return withContext(ioDispatcher){
            try{
                val response = managerService.getAllManager()
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
    suspend fun addManager( map : Map<String,String>) : ApiResult<Manager>{
        return withContext(ioDispatcher){
            try{
                val response = managerService.addManager(map)
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
                val response = managerService.deleteManager(id)
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
    fun getManager(id : Long) = managerList.first{manager -> manager.id == id}
    suspend fun updateManager(id : Long,fields : String) : ApiResult<Manager>{
        return withContext(ioDispatcher){
            try{
                val response = managerService.updateManager(id,fields)
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
}