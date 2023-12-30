package com.example.adminorderapp.api

import com.example.adminorderapp.util.DataStoreUtil
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class TokenInterceptor @Inject constructor(
    private val dataStoreUtil: DataStoreUtil
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newRequest = request.newBuilder().addHeader("Authorization","Bearer ${dataStoreUtil.getToken()}").build()
        return chain.proceed(newRequest)
    }
}