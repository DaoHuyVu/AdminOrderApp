package com.example.adminorderapp.data.store

import com.example.adminorderapp.util.Message
import com.example.adminorderapp.api.ApiResult
import com.example.adminorderapp.api.dispatchers.IODispatcher
import com.example.adminorderapp.api.store.Store
import com.example.adminorderapp.api.store.StoreService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import java.util.LinkedList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoreRepository @Inject constructor(
    private val storeService: StoreService,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) {
    private var storeList = LinkedList<Store>()
    private var unManagedStores = LinkedList<Store>()
    fun getAllStoreUiView() : List<StoreUiView>{
        return storeList.map{store -> store.toStoreUiView()}
    }
    suspend fun fetchStoreUiViewList() : ApiResult<List<StoreUiView>> {
        return withContext(ioDispatcher) {
            try {
                val response = storeService.getAllStore()
                if (response.isSuccessful) {
                    storeList = LinkedList(response.body()!!)
                    ApiResult.Success(response.body()!!.map { store -> store.toStoreUiView() })
                } else if(response.code() in 400 .. 500 ){
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
    suspend fun fetchUnManagedStoreUiViewList(isManaged : Boolean) : ApiResult<List<StoreUiView>> {
        return withContext(ioDispatcher) {
            try {
                val response = storeService.getAllStore(isManaged)
                if (response.isSuccessful) {
                    unManagedStores = LinkedList(response.body()!!)
                    ApiResult.Success(response.body()!!.map { store -> store.toStoreUiView() })
                } else if(response.code() in 400 .. 500 ){
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
    suspend fun addStore(fields : Map<String,String>) : ApiResult<Store>{
        return withContext(ioDispatcher){
            try{
                val response = storeService.addStore(fields)
                if(response.isSuccessful){
                    storeList.add(response.body()!!)
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
    suspend fun deleteStore(id : Long) : ApiResult<List<StoreUiView>>{
        return withContext(ioDispatcher) {
            try {
                val response = storeService.deleteStore(id)
                if (response.isSuccessful) {
                    storeList.remove(response.body()!!)
                    ApiResult.Success(storeList.toList().map { store -> store.toStoreUiView() })
                } else if (response.code() in 400..500) {
                    ApiResult.Error(Message.LOAD_ERROR)
                } else ApiResult.Error(Message.SERVER_BREAKDOWN)
            } catch (ex: UnknownHostException) {
                ApiResult.Error(Message.NO_INTERNET_CONNECTION)
            } catch (ex: Exception) {
                ApiResult.Exception(ex)
            }
        }
    }
    fun getStore(id : Long) = storeList.first{s -> s.id == id}
    suspend fun updateStore(id : Long,fields : String) : ApiResult<Store>{
        return withContext(ioDispatcher){
            try{
                val response = storeService.updateStore(id,fields)
                if(response.isSuccessful){
                    val index = storeList.indexOfFirst { i-> i.id == id }
                    storeList[index] = response.body()!!
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