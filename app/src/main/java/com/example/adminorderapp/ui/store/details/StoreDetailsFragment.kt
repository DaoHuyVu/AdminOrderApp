package com.example.adminorderapp.ui.store.details

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
import com.example.adminorderapp.databinding.FragmentStoreDetailsBinding
import com.example.adminorderapp.ui.TimePickerDialogFragment
import com.example.adminorderapp.util.Message
import com.example.adminorderapp.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StoreDetailsFragment : Fragment() {
    private var _binding : FragmentStoreDetailsBinding? = null
    private val binding get() = _binding!!
    @Inject lateinit var factory : StoreDetailsViewModel.StoreDetailsFactory
    private val args by navArgs<StoreDetailsFragmentArgs>()
    private val viewModel by viewModels<StoreDetailsViewModel>(
        factoryProducer = {StoreDetailsViewModel.provideFactory(factory,args.id)}
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoreDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            city.setText(viewModel.city)
            city.doOnTextChanged { text,_,_,_ -> viewModel.city = text.toString()}
            district.setText(viewModel.district)
            district.doOnTextChanged { text,_,_,_ -> viewModel.district = text.toString()}
            street.setText(viewModel.street)
            street.doOnTextChanged { text,_,_,_ -> viewModel.street = text.toString()}
            openingTimeButton.setOnClickListener{
                TimePickerDialogFragment{ hour,minute ->
                    val time = getString(R.string.time_display,hour,minute)
                    openingTime.text = time
                    viewModel.openingTime = time
                }.show(parentFragmentManager,"openingTimeDialog")
            }
            closingTimeButton.setOnClickListener{
                TimePickerDialogFragment{ hour,minute ->
                    val time = getString(R.string.time_display,hour,minute)
                    closingTime.text = time
                    viewModel.closingTime = time
                }.show(parentFragmentManager,"closingTimeDialog")
            }
            addButton.setOnClickListener{
                viewModel.updateStore()
            }
            openingTime.text = viewModel.openingTime
            closingTime.text = viewModel.closingTime
        }
        viewModel.uiState.observe(viewLifecycleOwner){
            binding.progressBar.visibility = if(it.isLoading) View.VISIBLE else View.GONE
            it.message?.let { message ->
                when(message){
                    Message.NO_INTERNET_CONNECTION -> showToast(getString(R.string.no_internet_connection))
                    Message.SERVER_BREAKDOWN -> showToast(getString(R.string.server_breakdown))
                    Message.BAD_REQUEST -> showToast(getString(R.string.bad_request))
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