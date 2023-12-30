package com.example.adminorderapp.ui.shipper.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.adminorderapp.api.ApiResult
import com.example.adminorderapp.data.shipper.ShipperRepository
import com.example.adminorderapp.ui.UiState
import com.example.adminorderapp.util.Message
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class ShipperDetailsViewModel @AssistedInject constructor(
    @Assisted private val id : Long,
    private val shipperRepository: ShipperRepository
) : ViewModel(){
    private val _uiState = MutableLiveData(UiState())
    private val shipper = shipperRepository.getShipper(id)
    val uiState get() = _uiState
    var email = shipper.email
        private set
    var name = shipper.name
        private set
    var phone = shipper.phone
        private set
    var salary = shipper.salary
        private set
    var dateOfBirth = shipper.dateOfBirth
        private set
    var genderName = shipper.gender
    private var genderId = -1
    fun onEmailChange(e : String){
        email = e
    }
    fun onNameChange(n : String){
        name = n
    }
    fun onPhoneChange(p : String){
        phone = p
    }
    fun onSalaryChange(s : String){
        salary = s
    }
    fun onMaleSelected(id : Int){
        genderName = "MALE"
        genderId = id
    }
    fun onFemaleSelected(id : Int){
        genderName = "FEMALE"
        genderId = id
    }
    fun dateOfBirthChange(dob : String){
        dateOfBirth = dob
    }
    fun messageShown(){
        _uiState.value = _uiState.value?.copy(message = null)
    }
    fun updateManager(){
        val map = HashMap<String,String>()
        if(email != shipper.email) map["email"] = email
        if(name != shipper.name) map["name"] = name
        if(phone != shipper.phone) map["phone"] = phone
        if(salary != shipper.salary) map["salary"] = salary
        if(dateOfBirth != shipper.dateOfBirth) map["dateOfBirth"] = dateOfBirth
        if(genderName != shipper.gender) map["gender"] = genderName
        val gson = Gson()
        val fields = gson.toJson(map)
        _uiState.value = _uiState.value?.copy(isLoading = true)
        viewModelScope.launch {
            when(val result = shipperRepository.updateShipper(id,fields)){
                is ApiResult.Success -> _uiState.value = _uiState.value?.copy(isSuccessful = true, isLoading = false)
                is ApiResult.Error -> _uiState.value = _uiState.value?.copy(message = result.message, isLoading = false)
                is ApiResult.Exception -> _uiState.value = _uiState.value?.copy(message = Message.SERVER_BREAKDOWN, isLoading = false)
            }
        }
    }

    @AssistedFactory
    interface ShipperDetailsFactory{
        fun create(id : Long) : ShipperDetailsViewModel
    }
    companion object{
        fun provideFactory(
            shipperDetailsFactory: ShipperDetailsFactory,
            id : Long
        ) = object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("Unchecked_cast")
                return shipperDetailsFactory.create(id) as T
            }
        }
    }
}