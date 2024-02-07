package com.example.adminorderapp.ui.shipper.create

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminorderapp.api.ApiResult
import com.example.adminorderapp.data.staff.StaffRepository
import com.example.adminorderapp.data.store.StoreRepository
import com.example.adminorderapp.data.store.StoreUiView
import com.example.adminorderapp.ui.UiState
import com.example.adminorderapp.util.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateShipperViewModel @Inject constructor(
    private val staffRepository: StaffRepository,
    private val storeRepository: StoreRepository
) : ViewModel(){
    private val _uiState = MutableLiveData(UiState())
    init{
        handleCallback{ storeRepository.fetchStoreUiViewList() }
    }
    private val _stores = MutableLiveData<List<StoreUiView>?>()
    val stores get() = _stores
    val uiState get() = _uiState
    var email = ""
        private set
    var password = ""
        private set
    var name = ""
        private set
    var phone = ""
        private set
    var salary = ""
        private set
    var dateOfBirth = ""
        private set
    private var genderName = "MALE"
    var genderId = -1
        private set
    var storePosition = 0
        private set

    fun onEmailChange(e : String){
        email = e
    }
    fun onPasswordChange(pw : String){
        password = pw
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
    fun storeChange(position : Int){
        storePosition = position
    }
    fun messageShown(){
        _uiState.value = _uiState.value?.copy(message = null)
    }
    fun addShipper(){
        val map = HashMap<String,String>()
        map["email"] = email
        map["password"] = password
        map["name"] = name
        map["phone"] = phone
        map["salary"] = salary
        map["dateOfBirth"] = dateOfBirth
        map["gender"] = genderName
        map["storeId"] = _stores.value!![storePosition].id.toString()
        map["role"] = "ROLE_STAFF"
        _uiState.value = _uiState.value?.copy(isLoading = true)
        viewModelScope.launch {
            when(val result = staffRepository.addStaff(map)){
                is ApiResult.Success -> _uiState.value = _uiState.value?.copy(isSuccessful = true, isLoading = false)
                is ApiResult.Error -> _uiState.value = _uiState.value?.copy(message = result.message, isLoading = false)
                is ApiResult.Exception -> _uiState.value = _uiState.value?.copy(message = Message.SERVER_BREAKDOWN, isLoading = false)
            }
        }
    }
    private fun handleCallback(callback : suspend () -> ApiResult<List<StoreUiView>>){
        _uiState.value = _uiState.value?.copy(isLoading = true)
        viewModelScope.launch {
            when(val result = callback.invoke()){
                is ApiResult.Success -> {
                    _stores.value = result.data
                    _uiState.value = UiState()
                }
                is ApiResult.Error -> _uiState.value = _uiState.value?.copy(message = result.message, isLoading = false)
                is ApiResult.Exception -> _uiState.value = _uiState.value?.copy(message = Message.SERVER_BREAKDOWN, isLoading = false)
            }
        }
    }
}