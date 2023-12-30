package com.example.adminorderapp.ui.store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.adminorderapp.util.Message
import com.example.adminorderapp.R
import com.example.adminorderapp.databinding.FragmentStoreBinding
import com.example.adminorderapp.ui.BottomSheetDialogFragmentManager
import com.example.adminorderapp.ui.HomeFragmentDirections
import com.example.adminorderapp.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoreFragment : Fragment() {
    private var _binding : FragmentStoreBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<StoreViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoreBinding.inflate(inflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = true
                viewModel.fetchStoreList()
            }
        }
        val adapter = StoreAdapter{ id,name ->
            BottomSheetDialogFragmentManager.getInstance().getDialog(
                name,
                {findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToStoreDetailsFragment(id))},
                { viewModel.deleteStore(id) }
            ).show(childFragmentManager,"StoreFragment")
        }
        binding.storeList.adapter = adapter
        viewModel.storeUiState.observe(viewLifecycleOwner){
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
        viewModel.getStoreList()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}