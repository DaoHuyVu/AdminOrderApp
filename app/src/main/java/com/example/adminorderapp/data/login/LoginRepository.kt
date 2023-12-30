package com.example.adminorderapp.data.login

import com.example.adminorderapp.util.DataStoreUtil
import com.example.adminorderapp.util.Message
import com.example.adminorderapp.util.ROLE
import com.example.adminorderapp.api.ApiResult
import com.example.adminorderapp.api.login.AuthResponse
import com.example.adminorderapp.api.login.AuthService
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository @Inject constructor(
    private val dataStoreUtil: DataStoreUtil,
    private val authService: AuthService
){
    suspend fun login(userName : String , password : String) : ApiResult<AuthResponse> {
        val fields = hashMapOf<String,String>()
        fields["userName"] = userName
        fields["password"] = password
        val response = authService.login(fields)
        return try{
            if(response.isSuccessful){
               if(response.body()!!.role[0] != ROLE)
                   ApiResult.Error(Message.BAD_REQUEST)
                else{
                   dataStoreUtil.saveUserInfo(response.body()!!.accessToken)
                   ApiResult.Success(response.body()!!)
               }
            }
            else if(response.code() == 400)
                ApiResult.Error(Message.BAD_REQUEST)
            else ApiResult.Error(Message.SERVER_BREAKDOWN)
        }catch(ex : UnknownHostException){
            ApiResult.Error(Message.NO_INTERNET_CONNECTION)
        }
        catch (ex : Exception){
            ApiResult.Exception(ex)
        }
    }
}