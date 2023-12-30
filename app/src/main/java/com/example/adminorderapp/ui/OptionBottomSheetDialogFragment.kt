package com.example.adminorderapp.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.adminorderapp.R
import com.example.adminorderapp.databinding.OptionBottomSheetLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class OptionBottomSheetDialogFragment(
    private val name : String,
    private val details : () -> Unit,
    private val delete : () -> Unit
) : BottomSheetDialogFragment(){
    private var _binding : OptionBottomSheetLayoutBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = OptionBottomSheetLayoutBinding.inflate(inflater)
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
                AlertDialog
                    .Builder(requireContext())
                    .setTitle(getString(R.string.delete_item))
                    .setMessage(getString(R.string.delete_message))
                    .setPositiveButton(getString(R.string.cancel_label)) {
                            dialog, _ -> dialog.dismiss()
                    }
                    .setNegativeButton(getString(R.string.yes_label)){ _, _ ->
                        delete.invoke()
                        dismiss()
                    }.create().show()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}