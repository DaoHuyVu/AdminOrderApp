package com.example.adminorderapp.ui.revenue

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import com.example.adminorderapp.R
import com.example.adminorderapp.databinding.FragmentRevenueBinding
import com.example.adminorderapp.util.CustomValueFormatter
import com.example.adminorderapp.util.Message
import com.example.adminorderapp.util.config
import com.example.adminorderapp.util.showToast
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import kotlin.random.Random

@AndroidEntryPoint
class RevenueFragment : Fragment() {
    private var _binding : FragmentRevenueBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<RevenueViewModel>()
    private lateinit var phaseArray : List<String>
    private lateinit var monthArray : List<Int>
    private lateinit var yearArray : List<Int>
    private lateinit var dayArray : List<Int>
    private val calendar = Calendar.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRevenueBinding.inflate(inflater)
        phaseArray = resources.getStringArray(R.array.by_array).toList()
        monthArray = resources.getIntArray(R.array.month_array).toList()
        val currentYear = calendar.get(Calendar.YEAR)
        yearArray = (currentYear downTo  currentYear-2).toList()
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            byPhaseSpinner.adapter = provideSpinnerAdapter(phaseArray)
            byPhaseSpinner.setSelection(viewModel.selectedPhasePosition)
            byPhaseSpinner.onItemSelectedListener = provideOnItemSelectedListener {
                    position -> viewModel.onPhasePositionChange(position)

            }
            yearSpinner.adapter = provideSpinnerAdapter(yearArray)
            yearSpinner.setSelection(viewModel.selectedYearPosition)
            yearSpinner.onItemSelectedListener = provideOnItemSelectedListener {
                    position -> viewModel.onSelectedYearPositionChange(position)
                    setUpDaySpinner()
            }
            monthSpinner.adapter = provideSpinnerAdapter(monthArray)
            monthSpinner.setSelection(viewModel.selectedMonthPosition)
            monthSpinner.onItemSelectedListener = provideOnItemSelectedListener {
                    position -> viewModel.onSelectedMonthPositionChange(position)
                    setUpDaySpinner()
            }
            
            setUpDaySpinner()
            swipeRefreshLayout.setOnRefreshListener {
                viewModel.getRevenues()
                swipeRefreshLayout.isRefreshing = true
            }

            daySpinner.onItemSelectedListener = provideOnItemSelectedListener {
                    position -> viewModel.onSelectedDayPositionChange(position)
            }

            overallBarChart.config()
            menuItemBarChart.config()
        }

        viewModel.selectedPhasePositionLiveData.observe(viewLifecycleOwner){
            when(it){
                2 -> {
                    binding.apply {
                        daySpinner.visibility = View.INVISIBLE
                        monthSpinner.visibility = View.INVISIBLE
                        yearSpinner.visibility = View.VISIBLE
                    }
                }
                1 -> {
                    binding.apply {
                        daySpinner.visibility = View.INVISIBLE
                        monthSpinner.visibility = View.VISIBLE
                        yearSpinner.visibility = View.VISIBLE
                    }
                }
                0 -> {
                    binding.apply {
                        daySpinner.visibility = View.VISIBLE
                        monthSpinner.visibility = View.VISIBLE
                        yearSpinner.visibility = View.VISIBLE
                    }
                }
            }
        }
        viewModel.revenueData.observe(viewLifecycleOwner){
            binding.progressHorizontal.visibility = if(it.isLoading) View.VISIBLE else View.INVISIBLE
            it.message?.let { message ->
                when(message){
                    Message.NO_INTERNET_CONNECTION -> showToast(getString(R.string.no_internet_connection))
                    Message.SERVER_BREAKDOWN -> showToast(getString(R.string.server_breakdown))
                    Message.BAD_REQUEST -> showToast(getString(R.string.bad_request))
                    else -> throw IllegalStateException()
                }
                viewModel.messageShown()
                binding.swipeRefreshLayout.isRefreshing = false
            }
            if(!it.isLoading){
                binding.swipeRefreshLayout.isRefreshing = false
                val barDataSets = arrayListOf<BarDataSet>()
                val random = Random
                it.overallRevenue.forEachIndexed{index,item ->
                    val entries = arrayListOf<BarEntry>()
                    entries.add(BarEntry(index.toFloat(),item.total.toFloat()))
                    val barDataSet = BarDataSet(entries,item.store)
                    barDataSet.valueTextColor = Color.BLACK
                    barDataSet.valueTextSize = 16f
                    barDataSet.color = Color.rgb(random.nextInt(256),random.nextInt(256),random.nextInt(256))
                    barDataSets.add(barDataSet)
                }
                val barData = BarData(barDataSets.toList())
                binding.apply {
                    overallBarChart.apply {
                        data = barData
                        data.barWidth = 0.9f
                        data.setValueFormatter(CustomValueFormatter.getInstance())
                        animateY(1000)
                    }
                }
            }
        }
        viewModel.menuItemRevenue.observe(viewLifecycleOwner){
            it?.let{
                val random = Random
                val barDataSets = arrayListOf<BarDataSet>()
                it.forEachIndexed { index, revenue ->
                    val barEntries = arrayListOf(BarEntry(index.toFloat(),revenue.total.toFloat()))
                    val barDataSet = BarDataSet(barEntries,revenue.store)
                    barDataSet.valueTextColor = Color.BLACK
                    barDataSet.valueTextSize = 16f
                    barDataSet.color = Color.rgb(random.nextInt(256),random.nextInt(256),random.nextInt(256))
                    barDataSets.add(barDataSet)
                }
                val barData = BarData(barDataSets.toList())
                binding.menuItemBarChart.apply {
                    data = barData
                    data.barWidth = 0.9f
                    data.setValueFormatter(CustomValueFormatter.getInstance())
                    animateY(1000)
                }
            }
        }
        viewModel.menuItems.observe(viewLifecycleOwner){
            it?.let{
                binding.menuItemSpinner.apply {
                    adapter = provideSpinnerAdapter(it)
                    setSelection(viewModel.selectedMenuItemPosition)
                    onItemSelectedListener = provideOnItemSelectedListener {
                        position -> viewModel.onMenuItemChange(position)
                    }
                }

            }
        }
        viewModel.getCategories()
        viewModel.getRevenues()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
    private fun <T> provideSpinnerAdapter(array : List<T>) : ArrayAdapter<T>{
        val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,array)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return adapter
    }
    private fun provideOnItemSelectedListener(callback: (Int) -> Unit) : AdapterView.OnItemSelectedListener{
        return object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                callback.invoke(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
    private fun getDaysOfMonth(month : Int,year : Int) : Int{
        calendar.set(Calendar.DAY_OF_MONTH,1)
        calendar.set(Calendar.MONTH,month-1)
        calendar.set(Calendar.YEAR,year)
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }
    private fun setUpDaySpinner(){
        binding.apply {
            val dayOfMonth = getDaysOfMonth(
                monthArray[monthSpinner.selectedItemPosition],
                yearArray[yearSpinner.selectedItemPosition]
            )
            dayArray = (1..dayOfMonth).toList()
            daySpinner.adapter = provideSpinnerAdapter(dayArray)
            // if selected day is smaller than or equal the day of month then we
            // can safely select it again in the new spinner array, otherwise it would cause
            // IndexOutOfBoundsException
            val position =
                if(viewModel.selectedDayPosition + 1 <= dayOfMonth) viewModel.selectedDayPosition
                else 0
            daySpinner.setSelection(position)
        }
    }
}