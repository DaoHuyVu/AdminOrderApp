package com.example.adminorderapp.ui.menuItem.create


import android.os.Bundle

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.adminorderapp.R
import com.example.adminorderapp.databinding.FragmentCreateMenuItemBinding
import com.example.adminorderapp.util.Message
import com.example.adminorderapp.util.UriResolver
import com.example.adminorderapp.util.showToast
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateMenuItemFragment : Fragment() {
    private var _binding : FragmentCreateMenuItemBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<CreateMenuItemViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateMenuItemBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            itemName.setText(viewModel.name)
            itemName.doOnTextChanged { text, _, _, _ -> viewModel.onNameChange(text.toString())}
            itemPrice.setText(viewModel.price)
            itemPrice.doOnTextChanged { text, _, _, _ -> viewModel.onPriceChange(text.toString())}
            itemDescription.setText(viewModel.description)
            itemDescription.doOnTextChanged {
                    text, _, _, _ -> viewModel.onDescriptionChange(text.toString())
            }
            imageUri.text = viewModel.imageUri
            val launcher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){uri ->
                uri?.let{
                    imageUri.text = uri.toString()
                    val part = UriResolver.getPartFromUri(
                        uri,
                        requireActivity().contentResolver,
                        itemName.text.toString().lowercase()
                    )
                    viewModel.onImageSelected(uri.toString(),part)
                }
            }
            imagePickerButton.setOnClickListener {
                launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
        viewModel.uiState.observe(viewLifecycleOwner){
            binding.progressBar.visibility = if(it.isLoading) View.VISIBLE else View.GONE
            it.message?.let { message ->
                when(message){
                    Message.NO_INTERNET_CONNECTION -> showToast(getString(R.string.no_internet_connection))
                    Message.SERVER_BREAKDOWN -> showToast(getString(R.string.server_breakdown))
                    Message.LOAD_ERROR -> showToast(getString(R.string.load_error))
                    else -> throw IllegalStateException()
                }
                viewModel.messageShown()
            }
            if(it.isSuccessful){
                showToast(getString(R.string.added_successfully))
                findNavController().popBackStack()
            }
        }
        viewModel.category.observe(viewLifecycleOwner){
            it?.forEach {category ->
                val chip = Chip(requireActivity())
                chip.text = category.name
                chip.width = LayoutParams.WRAP_CONTENT
                chip.height = LayoutParams.WRAP_CONTENT
                chip.isCheckable = true
                chip.isCheckedIconVisible = true
                chip.isChecked = viewModel.isCategoryChecked(category)
                chip.setOnCheckedChangeListener { _, isChecked ->
                    if(isChecked)
                        viewModel.onCategorySelected(category)
                    else
                        viewModel.onCategoryUnSelected(category)
                }
                binding.chipGroup.addView(chip)
            }
            binding.addButton.setOnClickListener{
                viewModel.addItem()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}