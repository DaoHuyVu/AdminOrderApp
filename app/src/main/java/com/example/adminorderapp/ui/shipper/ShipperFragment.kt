package com.example.adminorderapp.ui.shipper

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.adminorderapp.util.Message
import com.example.adminorderapp.R
import com.example.adminorderapp.databinding.FragmentShipperBinding
import com.example.adminorderapp.ui.BottomSheetDialogFragmentManager
import com.example.adminorderapp.ui.HomeFragmentDirections
import com.example.adminorderapp.util.showToast
import com.example.adminorderapp.ui.manager.StaffAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShipperFragment : Fragment() {
    private var _binding : FragmentShipperBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ShipperViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShipperBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = true
                viewModel.fetchShipperList()
            }
        }
        val adapter = StaffAdapter{id,name ->
            BottomSheetDialogFragmentManager.getInstance().getDialog(
                name,
                {findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToShipperDetailsFragment(id))}
            ){
                viewModel.deleteShipper(id)
            }.show(parentFragmentManager,"ShipperFragment")
        }
        binding.shipperList.adapter = adapter
        viewModel.managerUiState.observe(viewLifecycleOwner){
            binding.apply {
                progressBar.visibility = if(it.isLoading) View.VISIBLE else View.GONE
                it.message?.let {message ->
                    when(message){
                        Message.NO_INTERNET_CONNECTION -> showToast(getString(R.string.no_internet_connection))
                        Message.SERVER_BREAKDOWN -> showToast(getString(R.string.server_breakdown))
                        Message.LOAD_ERROR -> showToast(getString(R.string.load_error))
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
        viewModel.getShipperList()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}