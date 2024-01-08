package com.example.adminorderapp.ui.menuItem.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.adminorderapp.R
import com.example.adminorderapp.databinding.FragmentMenuItemDetailsBinding
import com.example.adminorderapp.util.Message
import com.example.adminorderapp.util.showToast
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MenuItemDetailsFragment : Fragment() {
    private var _binding : FragmentMenuItemDetailsBinding? = null
    private val binding get() = _binding!!
    @Inject lateinit var factory : MenuItemDetailsViewModel.MenuItemDetailsFactory
    private val args by navArgs<MenuItemDetailsFragmentArgs>()
    private val viewModel by viewModels<MenuItemDetailsViewModel>(
        factoryProducer = {MenuItemDetailsViewModel.provideFactory(factory,args.id)}
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuItemDetailsBinding.inflate(inflater)
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
            itemUrl.setText(viewModel.imageUrl)
            itemUrl.doOnTextChanged {
                    text, _, _, _ -> viewModel.onUrlChange(text.toString())
            }

        }
        viewModel.uiState.observe(viewLifecycleOwner){
            binding.progressBar.visibility = if(it.isLoading) View.VISIBLE else View.GONE
            it.message?.let { message ->
                when(message){
                    Message.NO_INTERNET_CONNECTION -> showToast(getString(R.string.no_internet_connection))
                    Message.SERVER_BREAKDOWN -> showToast(getString(R.string.server_breakdown))
                    Message.LOAD_ERROR -> showToast(getString(R.string.update_failed))
                    else -> throw IllegalStateException()
                }
                viewModel.messageShown()
            }
            if(it.isSuccessful){
                showToast(getString(R.string.updated_successfully))
                findNavController().popBackStack()
            }
        }
        viewModel.category.observe(viewLifecycleOwner){
            it?.forEach {category ->
                val chip = Chip(requireActivity())
                chip.text = category.name
                chip.width = WindowManager.LayoutParams.WRAP_CONTENT
                chip.height = WindowManager.LayoutParams.WRAP_CONTENT
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
                viewModel.updateItem()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}