package com.example.adminorderapp.ui.manager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.adminorderapp.util.Message
import com.example.adminorderapp.R
import com.example.adminorderapp.databinding.FragmentManagerBinding
import com.example.adminorderapp.ui.BottomSheetDialogFragmentManager
import com.example.adminorderapp.ui.HomeFragmentDirections
import com.example.adminorderapp.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManagerFragment : Fragment() {
    private var _binding : FragmentManagerBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ManagerViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManagerBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            swipeRefreshLayout.setOnRefreshListener{
                swipeRefreshLayout.isRefreshing = true
                viewModel.fetchManagerList()
            }
        }
        val adapter = StaffAdapter{ id,name ->
            BottomSheetDialogFragmentManager.getInstance().getDialog(
                name,
                { findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToManagerDetailsFragment(id)) },
            ){
                viewModel.deleteManager(id)
            }.show(parentFragmentManager,"ManagerFragment")
        }
        binding.managerList.adapter = adapter
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
                    viewModel.messageShown()
                    swipeRefreshLayout.isRefreshing = false
                }
                if(!it.isLoading){
                    adapter.submitList(it.data)
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        }
        viewModel.getManagerList()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}