package com.example.adminorderapp.api

import com.example.adminorderapp.util.Message

sealed class ApiResult<out T>{
    data class Success<T>( val data : T) : ApiResult<T>()
    data class Error( val message : Message) : ApiResult<Nothing>()
    data class Exception(val throwable: Throwable) : ApiResult<Nothing>()
}
