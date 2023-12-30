package com.example.adminorderapp.util

import com.github.mikephil.charting.charts.BarChart

fun BarChart.config() {
    axisLeft.valueFormatter = CustomValueFormatter.getInstance()
    axisRight.isEnabled = false
    description.isEnabled = false
    xAxis.isEnabled = false
    legend.isWordWrapEnabled = true
    legend.maxSizePercent = 0.5f
}