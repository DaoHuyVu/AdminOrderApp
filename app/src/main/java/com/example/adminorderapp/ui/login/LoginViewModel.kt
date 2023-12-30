package com.example.adminorderapp.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminorderapp.util.Message
import com.example.adminorderapp.api.ApiResult
import com.example.adminorderapp.data.login.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {
    private var _loginUiState = MutableLiveData(LoginUiState())
    val loginUiState get() = _loginUiState
    var userName = ""
        private set
    var password = ""
        private set
    fun login(){
        _loginUiState.value = LoginUiState(isLoading = true)
        viewModelScope.launch {
            when(val result = loginRepository.login(userName,password)){
                is ApiResult.Success -> _loginUiState.value = LoginUiState(isUserLoggedIn = true)
                is ApiResult.Error -> _loginUiState.value = LoginUiState(message = result.message)
                is ApiResult.Exception -> _loginUiState.value = LoginUiState(message = Message.SERVER_BREAKDOWN)
            }
        }
    }
    fun messageShown(){
        _loginUiState.value = LoginUiState()
    }
    fun userNameChange(un : String){
        userName = un
    }
    fun passwordChange(pw : String){
        password = pw
    }
}