package com.example.adminorderapp.ui.category.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.adminorderapp.R
import com.example.adminorderapp.databinding.FragmentCategoryDetailsBinding
import com.example.adminorderapp.util.Message
import com.example.adminorderapp.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
@AndroidEntryPoint
class CategoryDetailsFragment : Fragment() {
    private var _binding : FragmentCategoryDetailsBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<CategoryDetailsFragmentArgs>()
    @Inject lateinit var factory : CategoryDetailsViewModel.CategoryDetailsFactory
    private val viewModel by viewModels<CategoryDetailsViewModel>(
        factoryProducer = { CategoryDetailsViewModel.provideFactory(factory,args.id) }
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            categoryName.setText(viewModel.name)
            categoryName.doOnTextChanged { text, _, _, _ -> viewModel.onNameChange(text.toString())}
            categoryImageUrl.setText(viewModel.imageUrl)
            categoryImageUrl.doOnTextChanged { text, _, _, _ -> viewModel.onUrlChange(text.toString())}
            updateButton.setOnClickListener {
                viewModel.updateItem()
            }
        }
        viewModel.uiState.observe(viewLifecycleOwner){
            binding.progressBar.visibility = if(it.isLoading) View.VISIBLE else View.GONE
            it.message?.let { message ->
                when(message){
                    Message.BAD_REQUEST -> showToast(getString(R.string.bad_request))
                    Message.SERVER_BREAKDOWN -> showToast(getString(R.string.server_breakdown))
                    Message.NO_INTERNET_CONNECTION -> showToast(getString(R.string.no_internet_connection))
                    else -> throw IllegalStateException()
                }
                viewModel.messageShown()
            }
            if(it.isSuccessful){
                showToast(getString(R.string.updated_successfully))
                findNavController().popBackStack()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}