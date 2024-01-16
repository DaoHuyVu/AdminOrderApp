package com.example.adminorderapp.data.menuItem

import com.example.adminorderapp.util.Message
import com.example.adminorderapp.api.ApiResult
import com.example.adminorderapp.api.category.Category
import com.example.adminorderapp.api.dispatchers.IODispatcher
import com.example.adminorderapp.api.menuItem.MenuItem
import com.example.adminorderapp.api.menuItem.MenuItemService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.net.UnknownHostException
import java.util.LinkedList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MenuItemRepository @Inject constructor(
    private val menuItemService: MenuItemService,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) {
    private var menuItemList = LinkedList<MenuItem>()
    fun getMenuItemUiViewList(category : Category? = null) : List<MenuItemUiView>{
        return if(category == null) menuItemList.toList().map{item -> item.toMenuItemUiView()}
        else  menuItemList.filter { item -> item.categories.contains(category) }.toList().map{item -> item.toMenuItemUiView()}
    }
    suspend fun fetchMenuItemUiViewList() : ApiResult<List<MenuItemUiView>> {
        return withContext(ioDispatcher){
            try{
                val response = menuItemService.getAllMenuItem()
                if(response.isSuccessful){
                    menuItemList = LinkedList(response.body()!!)
                    ApiResult.Success(menuItemList.toList().map { item -> item.toMenuItemUiView()})
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
    suspend fun addItem(
        name : RequestBody,
        price : RequestBody,
        description : RequestBody,
        part : MultipartBody.Part,
        categories : RequestBody,
    ) : ApiResult<MenuItem>{
        return withContext(ioDispatcher){
            try{
                val response = menuItemService.addMenuItem(name,price,description,part,categories)
                if(response.isSuccessful){
                    menuItemList.add(response.body()!!)
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
    suspend fun deleteMenuItem(id : Long) : ApiResult<List<MenuItemUiView>>{
        return withContext(ioDispatcher){
            try{
                val response = menuItemService.deleteMenuItem(id)
                if(response.isSuccessful){
                    // Remove response.body does not work since the response return menuItem not menuItemDto
                    menuItemList.removeAt(menuItemList.indexOfFirst { item -> item.id == response.body()!!.id })
                    ApiResult.Success(menuItemList.toList().map{item -> item.toMenuItemUiView()})
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
    fun getMenuItem(id : Long) = menuItemList.first{item -> item.id == id}
    suspend fun updateMenuItem(
        id : Long,
        fields : RequestBody? = null,
        image : MultipartBody.Part? = null
    ) : ApiResult<MenuItem>{
        return withContext(ioDispatcher){
            try{
                val response = menuItemService.updateMenuItem(id,fields,image)
                if(response.isSuccessful){
                    val index = menuItemList.indexOfFirst { item -> item.id == id }
                    menuItemList[index] = response.body()!!
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
    suspend fun getMenuItemName() : List<String> {
        if(menuItemList.isEmpty()){
            fetchMenuItemUiViewList()
        }
        return menuItemList.map { item -> item.name }
    }
}