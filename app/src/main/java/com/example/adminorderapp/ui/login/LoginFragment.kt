package com.example.adminorderapp.ui.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.adminorderapp.OnNavigateToHomeListener
import com.example.adminorderapp.util.Message
import com.example.adminorderapp.R
import com.example.adminorderapp.databinding.FragmentLoginBinding
import com.example.adminorderapp.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding : FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var listener: OnNavigateToHomeListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            listener = context as OnNavigateToHomeListener
        }catch (ex : Exception){
            Log.e("LoginFragment","Must implement the interface first")
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            usernameInput.setText( viewModel.userName)
            passwordInput.setText(viewModel.password)
            usernameInput.doOnTextChanged { text,_,_,_ -> viewModel.userNameChange(text.toString())}
            passwordInput.doOnTextChanged { text,_,_,_ -> viewModel.passwordChange(text.toString())}
            loginButton.setOnClickListener {
                viewModel.login()
            }
        }
        viewModel.loginUiState.observe(viewLifecycleOwner){
            binding.apply {
                loginProgressbar.visibility = if(it.isLoading) View.VISIBLE else View.GONE
                it.message?.let{
                    when(it){
                        Message.LOAD_ERROR -> showToast(getString(R.string.login_fail))
                        Message.SERVER_BREAKDOWN -> showToast(getString(R.string.server_breakdown))
                        Message.NO_INTERNET_CONNECTION -> showToast(getString(R.string.no_internet_connection))
                        else -> throw IllegalStateException()
                    }
                    viewModel.messageShown()
                }
                if(it.isUserLoggedIn) {
                    findNavController().apply {
                        listener.onNavigateToHome()
                        val graph = this.navInflater.inflate(R.navigation.home_graph)
                        this.graph = graph
                    }
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}