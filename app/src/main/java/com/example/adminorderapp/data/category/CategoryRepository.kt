package com.example.adminorderapp.data.category

import com.example.adminorderapp.api.ApiResult
import com.example.adminorderapp.api.category.Category
import com.example.adminorderapp.api.category.CategoryService
import com.example.adminorderapp.api.dispatchers.IODispatcher
import com.example.adminorderapp.util.Message
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import java.util.LinkedList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryService: CategoryService,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
){
    private var categories : LinkedList<Category>? = null
    suspend fun fetchCategory() : ApiResult<List<Category>>{
        return withContext(ioDispatcher){
            try{
                val response = categoryService.getAllCategory()
                if(response.isSuccessful){
                    categories = LinkedList(response.body()!!)
                    ApiResult.Success(categories!!.toList())
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
    suspend fun getCategories() : ApiResult<List<Category>> {
        if(categories == null){
            return fetchCategory()
        }
        return ApiResult.Success(categories!!.toList())
    }
    suspend fun addCategory(fields : Map<String,String>) : ApiResult<Category>{
        return withContext(ioDispatcher){
            try{
                val response = categoryService.addCategory(fields)
                if(response.isSuccessful){
                    categories?.add(response.body()!!)
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
    suspend fun deleteCategory(id : Long) : ApiResult<List<Category>>{
        return withContext(ioDispatcher){
            try{
                val response = categoryService.deleteCategory(id)
                if(response.isSuccessful){
                    categories?.remove(response.body()!!)
                    ApiResult.Success(categories!!.toList())
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
    suspend fun updateCategory(id : Long,fields : Map<String,String>) : ApiResult<Category>{
        return withContext(ioDispatcher){
            try{
                val response = categoryService.updateCategory(id,fields)
                if(response.isSuccessful){
                    val category = response.body()!!
                    val index = categories!!.indexOfFirst { c -> c.id == category.id }
                    categories!![index] = category
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
    fun getCategory(id : Long) : Category{
        return categories!!.first{c -> c.id == id}
    }
}