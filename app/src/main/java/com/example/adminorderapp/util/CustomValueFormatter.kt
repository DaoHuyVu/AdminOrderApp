package com.example.adminorderapp.util

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.NumberFormat
import java.util.Locale

class CustomValueFormatter private constructor(): ValueFormatter() {
    private val formatter = NumberFormat.getCurrencyInstance(Locale.US)
    override fun getBarLabel(barEntry: BarEntry?): String {
        return formatter.format(barEntry?.y)
    }

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return formatter.format(value)
    }
    companion object{
        @Volatile
        private var instance : CustomValueFormatter? = null
        fun getInstance() : CustomValueFormatter {
            return instance ?: synchronized(this){
                instance ?: CustomValueFormatter().also { instance = it }
            }
        }
    }
}