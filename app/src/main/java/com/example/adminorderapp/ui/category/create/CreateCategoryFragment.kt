package com.example.adminorderapp.ui.category.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.adminorderapp.R
import com.example.adminorderapp.databinding.FragmentCreateCategoryBinding
import com.example.adminorderapp.util.Message
import com.example.adminorderapp.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateCategoryFragment : Fragment(){
    private var _binding : FragmentCreateCategoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<CreateCategoryViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            categoryName.setText(viewModel.name)
            categoryName.doOnTextChanged { text, _, _, _ -> viewModel.onNameChange(text.toString())}
            categoryImageUrl.setText(viewModel.imageUrl)
            categoryImageUrl.doOnTextChanged { text, _, _, _ -> viewModel.onUrlChange(text.toString())}
            addButton.setOnClickListener {
                viewModel.addItem()
            }
        }
        viewModel.uiState.observe(viewLifecycleOwner){
            binding.progressBar.visibility = if(it.isLoading) View.VISIBLE else View.GONE
            it.message?.let { message ->
                when(message){
                    Message.BAD_REQUEST -> showToast(getString(R.string.add_failed))
                    Message.NO_INTERNET_CONNECTION -> showToast(getString(R.string.no_internet_connection))
                    Message.SERVER_BREAKDOWN -> showToast(getString(R.string.server_breakdown))
                    else -> throw IllegalStateException()
                }
                viewModel.messageShown()
            }
            if(it.isSuccessful){
                showToast(getString(R.string.added_successfully))
                findNavController().popBackStack()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}