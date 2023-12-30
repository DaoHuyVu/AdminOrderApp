package com.example.adminorderapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.adminorderapp.api.revenue.Revenue
import com.example.adminorderapp.databinding.RevenueBottomSheetLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class RevenueBottomSheetDialogFragment(
    private val name : String,
    private val details : () -> Unit,
    private val navigateStoreRevenue : () -> Unit,
    private val delete : () -> Unit
) : BottomSheetDialogFragment(){
    private var _binding : RevenueBottomSheetLayoutBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RevenueBottomSheetLayoutBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            itemName.text = name
            updateItem.setOnClickListener {
                details.invoke()
                dismiss()
            }
            deleteItem.setOnClickListener {
                delete.invoke()
                dismiss()
            }
            revenue.setOnClickListener {
                navigateStoreRevenue.invoke()
                dismiss()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}