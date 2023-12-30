package com.example.adminorderapp.ui.menuItem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.adminorderapp.util.Message
import com.example.adminorderapp.R
import com.example.adminorderapp.databinding.FragmentMenuItemBinding
import com.example.adminorderapp.ui.BottomSheetDialogFragmentManager
import com.example.adminorderapp.ui.HomeFragmentDirections
import com.example.adminorderapp.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuItemFragment : Fragment() {
    private var _binding : FragmentMenuItemBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MenuItemViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuItemBinding.inflate(inflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = true
                viewModel.fetchMenuItemList()
                appCompatSpinner.setSelection(0)
            }
            appCompatSpinner.setSelection(viewModel.position)
        }
        val adapter = MenuItemAdapter{ id,name ->
            BottomSheetDialogFragmentManager.getInstance().getDialog(
                name,
                {findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToMenuItemDetailFragment(id))},
            ){
                viewModel.deleteMenuItem(id)
            }.show(parentFragmentManager,"MenuItemFragment")
        }
        binding.menuItemList.adapter = adapter
        viewModel.menuItemUiState.observe(viewLifecycleOwner){
            binding.apply {
                progressBar.visibility = if(it.isLoading) View.VISIBLE else View.GONE
                it.message?.let {message ->
                    when(message){
                        Message.NO_INTERNET_CONNECTION -> showToast(getString(R.string.no_internet_connection))
                        Message.SERVER_BREAKDOWN -> showToast(getString(R.string.server_breakdown))
                        Message.BAD_REQUEST -> showToast(getString(R.string.bad_request))
                        else -> throw IllegalStateException()
                    }
                    swipeRefreshLayout.isRefreshing = false
                    viewModel.messageShown()
                }
                if(!it.isLoading){
                    adapter.submitList(it.data)
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        }
        viewModel.categories.observe(viewLifecycleOwner){
            it?.let{
                val categories = listOf("All",*it)
                val arrayAdapter = ArrayAdapter(
                    requireContext(),android.R.layout.simple_spinner_item,
                    categories)
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.appCompatSpinner.adapter = arrayAdapter
                binding.appCompatSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        viewModel.onChangeCategory(position)
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
        }
        viewModel.getMenuItemList(viewModel.selectedCategory)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}