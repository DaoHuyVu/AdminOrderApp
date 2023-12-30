package com.example.adminorderapp.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreUtil @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sharedPreferences = context.getSharedPreferences(SHARE_PREFERENCES,Context.MODE_PRIVATE)
    fun saveUserInfo(token : String){
        sharedPreferences.edit().apply{
            putString(TOKEN,token)
            apply()
        }
    }
    fun getToken() = sharedPreferences.getString(TOKEN,null)
    fun deleteUserInfo(){
        sharedPreferences.edit().apply{
            clear()
            apply()
        }
    }
}