package com.example.adminorderapp.ui.manager.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.adminorderapp.api.ApiResult
import com.example.adminorderapp.data.staff.StaffRepository
import com.example.adminorderapp.ui.UiState
import com.example.adminorderapp.util.Message
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class ManagerDetailsViewModel @AssistedInject constructor(
    @Assisted private val id : Long,
    private val staffRepository: StaffRepository
) : ViewModel(){
    private val _uiState = MutableLiveData(UiState())
    private val manager = staffRepository.getManager(id)
    val uiState get() = _uiState
    var email = manager.email
        private set
    var name = manager.name
        private set
    var phone = manager.phone
        private set
    var salary = manager.salary
        private set
    var dateOfBirth = manager.dateOfBirth
        private set
    var genderName = manager.gender
    var genderId = -1
        private set
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
        if(email != manager.email) map["email"] = email
        if(name != manager.name) map["name"] = name
        if(phone != manager.phone) map["phone"] = phone
        if(salary != manager.salary) map["salary"] = salary
        if(dateOfBirth != manager.dateOfBirth) map["dateOfBirth"] = dateOfBirth
        if(genderName != manager.gender) map["gender"] = genderName
        val gson = Gson()
        val fields = gson.toJson(map)
        _uiState.value = _uiState.value?.copy(isLoading = true)
        viewModelScope.launch {
            when(val result = staffRepository.updateStaff(id,fields)){
                is ApiResult.Success -> _uiState.value = _uiState.value?.copy(isSuccessful = true, isLoading = false)
                is ApiResult.Error -> _uiState.value = _uiState.value?.copy(message = result.message, isLoading = false)
                is ApiResult.Exception -> _uiState.value = _uiState.value?.copy(message = Message.SERVER_BREAKDOWN, isLoading = false)
            }
        }
    }

    @AssistedFactory
    interface ManagerDetailsFactory{
        fun create(id : Long) : ManagerDetailsViewModel
    }
    companion object{
        fun provideFactory(
            managerDetailsFactory: ManagerDetailsFactory,
            id : Long
        ) = object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("Unchecked_cast")
                return managerDetailsFactory.create(id) as T
            }
        }
    }
}