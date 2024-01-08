package com.example.adminorderapp.ui.shipper.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.adminorderapp.R
import com.example.adminorderapp.databinding.FragmentShipperDetailsBinding
import com.example.adminorderapp.ui.DatePickerDialogFragment
import com.example.adminorderapp.util.Message
import com.example.adminorderapp.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ShipperDetailsFragment : Fragment() {
    private var _binding : FragmentShipperDetailsBinding? = null
    private val binding get() = _binding!!
    @Inject lateinit var factory : ShipperDetailsViewModel.ShipperDetailsFactory
    private val args by navArgs<ShipperDetailsFragmentArgs>()
    private val viewModel by viewModels<ShipperDetailsViewModel>(
        factoryProducer = { ShipperDetailsViewModel.provideFactory(factory,args.id) }
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShipperDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            usernameInput.apply {
                this.setText(viewModel.name)
                doOnTextChanged { text, _, _, _ ->
                    viewModel.onNameChange(text.toString())
                }
            }
            emailInput.apply {
                this.setText(viewModel.email)
                doOnTextChanged { text, _, _, _ ->
                    viewModel.onEmailChange(text.toString())
                }
            }

            salaryInput.apply {
                this.setText(viewModel.salary)
                doOnTextChanged { text, _, _, _ ->
                    viewModel.onSalaryChange(text.toString())
                }
            }
            phoneInput.apply {
                this.setText(viewModel.phone)
                doOnTextChanged { text, _, _, _ ->
                    viewModel.onPhoneChange(text.toString())
                }
            }
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when(checkedId){
                    R.id.male_radio_button -> viewModel.onMaleSelected(checkedId)
                    R.id.female_radio_button -> viewModel.onFemaleSelected(checkedId)
                }
            }
            radioGroup.check(
                if(viewModel.genderName == "MALE") R.id.male_radio_button
                else R.id.female_radio_button)

            dateOfBirth.text = viewModel.dateOfBirth
            datePickerButton.setOnClickListener {
                DatePickerDialogFragment{ year,month,dayOfMonth ->
                    //Month start with 0
                    val text = getString(R.string.date_of_birth_display,dayOfMonth,month+1,year)
                    dateOfBirth.text = text
                    viewModel.dateOfBirthChange(text)
                }.show(parentFragmentManager,"datePicker")
            }
            addButton.setOnClickListener {
                viewModel.updateManager()
            }
        }
        viewModel.uiState.observe(viewLifecycleOwner){
            binding.apply {
                indicator.isIndeterminate = it.isLoading
                it.message?.let{message ->
                    when(message){
                        Message.NO_INTERNET_CONNECTION -> showToast(getString(R.string.no_internet_connection))
                        Message.SERVER_BREAKDOWN -> showToast(getString(R.string.server_breakdown))
                        Message.LOAD_ERROR -> showToast(getString(R.string.load_error))
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
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}