package com.example.adminorderapp.util

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavGraph

fun Fragment.showToast(message : String){
    Toast.makeText(requireActivity(),message,Toast.LENGTH_SHORT).show()
}


