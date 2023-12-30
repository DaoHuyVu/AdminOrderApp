package com.example.adminorderapp.data.menuItem

import com.example.adminorderapp.util.Message
import com.example.adminorderapp.api.ApiResult
import com.example.adminorderapp.api.category.Category
import com.example.adminorderapp.api.dispatchers.IODispatcher
import com.example.adminorderapp.api.menuItem.MenuItem
import com.example.adminorderapp.api.menuItem.MenuItemService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
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
                    ApiResult.Error(Message.BAD_REQUEST)
                }
                else ApiResult.Error(Message.SERVER_BREAKDOWN)
            }catch(ex : UnknownHostException){
                ApiResult.Error(Message.NO_INTERNET_CONNECTION)
            }catch (ex : Exception){
                ApiResult.Exception(ex)
            }
        }
    }
    suspend fun addItem(fields : Map<String,String>) : ApiResult<MenuItem>{
        return withContext(ioDispatcher){
            try{
                val response = menuItemService.addMenuItem(fields)
                if(response.isSuccessful){
                    menuItemList.add(response.body()!!)
                    ApiResult.Success(response.body()!!)
                }
                else if(response.code() in 400 .. 500 ){
                    ApiResult.Error(Message.BAD_REQUEST)
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
                    ApiResult.Error(Message.BAD_REQUEST)
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
    suspend fun updateMenuItem(id : Long,fields : String) : ApiResult<MenuItem>{
        return withContext(ioDispatcher){
            try{
                val response = menuItemService.updateMenuItem(id,fields)
                if(response.isSuccessful){
                    val index = menuItemList.indexOfFirst { item -> item.id == id }
                    menuItemList[index] = response.body()!!
                    ApiResult.Success(response.body()!!)
                }
                else if(response.code() in 400 .. 500 ){
                    ApiResult.Error(Message.BAD_REQUEST)
                }
                else ApiResult.Error(Message.SERVER_BREAKDOWN)
            }catch(ex : UnknownHostException){
                ApiResult.Error(Message.NO_INTERNET_CONNECTION)
            }catch (ex : Exception){
                ApiResult.Exception(ex)
            }
        }
    }
    fun getMenuItemName() = menuItemList.map { item -> item.name }
}