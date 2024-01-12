package com.example.adminorderapp.ui.shipper.create

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.adminorderapp.R
import com.example.adminorderapp.databinding.FragmentCreateShipperBinding
import com.example.adminorderapp.ui.DatePickerDialogFragment
import com.example.adminorderapp.util.Message
import com.example.adminorderapp.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateShipperFragment : Fragment() {
    private var _binding : FragmentCreateShipperBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<CreateShipperViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateShipperBinding.inflate(inflater)
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
            passwordInput.apply {
                this.setText(viewModel.password)
                doOnTextChanged { text, _, _, _ ->
                    viewModel.onPasswordChange(text.toString())
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
            radioGroup.check(viewModel.genderId)

            dateOfBirth.text = viewModel.dateOfBirth
            datePickerButton.setOnClickListener {
                DatePickerDialogFragment{ year,month,dayOfMonth ->
                    //Month start with 0
                    val text = getString(R.string.date_display,dayOfMonth,month+1,year)
                    dateOfBirth.text = text
                    viewModel.dateOfBirthChange(text)
                }.show(parentFragmentManager,"datePicker")
            }
            storePicker.setSelection(viewModel.storePosition)
            storePicker.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.storeChange(position)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
            addButton.setOnClickListener {
                viewModel.addManager()
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
                    showToast(getString(R.string.added_successfully))
                    findNavController().popBackStack()
                }
            }
        }
        viewModel.stores.observe(viewLifecycleOwner){
            it?.let{
                val arrayAdapter = ArrayAdapter(
                    requireActivity(),
                    android.R.layout.simple_spinner_item,
                    it)
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.storePicker.adapter = arrayAdapter
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}