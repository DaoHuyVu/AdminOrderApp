package com.example.adminorderapp.ui.revenue

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import com.example.adminorderapp.R
import com.example.adminorderapp.databinding.FragmentRevenueBinding
import com.example.adminorderapp.ui.DatePickerDialogFragment
import com.example.adminorderapp.util.CustomValueFormatter
import com.example.adminorderapp.util.Message
import com.example.adminorderapp.util.config
import com.example.adminorderapp.util.showToast
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

import dagger.hilt.android.AndroidEntryPoint
import java.math.RoundingMode
import kotlin.math.log
import kotlin.random.Random

@AndroidEntryPoint
class RevenueFragment : Fragment() {
    private var _binding : FragmentRevenueBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<RevenueViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRevenueBinding.inflate(inflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.apply {
            viewModel.from?.let{from.text = it}
            viewModel.to?.let{to.text = it}
            from.setOnClickListener {
                DatePickerDialogFragment{ year,month,day ->
                    from.text = getString(R.string.date_display,day,month+1,year)
                    viewModel.fromSelected(from.text.toString())
                }.show(parentFragmentManager,"DatePickerFragment")
            }
            to.setOnClickListener {
                DatePickerDialogFragment{ year,month,day->
                    to.text = getString(R.string.date_display,day,month+1,year)
                    viewModel.toSelected(to.text.toString())
                }.show(parentFragmentManager,"DatePickerFragment")
            }
            binding.swipeRefreshLayout.setOnRefreshListener {
                binding.swipeRefreshLayout.isRefreshing = true
                viewModel.refresh()
            }
            overallBarChart.config()
            menuItemBarChart.config()
            binding.menuItemSpinner.setSelection(viewModel.selectedMenuItemPosition)
            binding.menuItemSpinner.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                   viewModel.selectedMenuItemChange(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        viewModel.revenueUiState.observe(viewLifecycleOwner){
            binding.progressHorizontal.visibility = if(it.isLoading) View.VISIBLE else View.INVISIBLE
            it.message?.let { message ->
                when(message){
                    Message.NO_INTERNET_CONNECTION -> showToast(getString(R.string.no_internet_connection))
                    Message.SERVER_BREAKDOWN -> showToast(getString(R.string.server_breakdown))
                    Message.LOAD_ERROR -> showToast(getString(R.string.load_error))
                    else -> throw IllegalStateException()
                }
                viewModel.messageShown()
                binding.swipeRefreshLayout.isRefreshing = false
            }
            if(!it.isLoading){
                binding.swipeRefreshLayout.isRefreshing = false
                val total = it.overallRevenue.fold(0.0){ total,revenue ->
                    total + revenue.total
                }
                binding.overallRevenue.text = getString(R.string.total,String.format("%.2f",total))
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
                val total = it.fold(0.0){ total,revenue ->
                    total + revenue.total
                }
                binding.menuItemRevenue.text = getString(R.string.total,total.toString())
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
            binding.menuItemSpinner.apply {
                adapter = provideSpinnerAdapter(it)
            }
        }
    }
    private fun <T> provideSpinnerAdapter(list : List<T>) : ArrayAdapter<T>{
        val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return adapter
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}