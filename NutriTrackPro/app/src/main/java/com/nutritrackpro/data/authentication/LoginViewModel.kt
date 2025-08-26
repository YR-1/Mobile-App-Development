package com.nutritrackpro.data.authentication

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LoginViewModel(context: Context) : ViewModel() {

    private val authRepository: AuthRepository =
        AuthRepository(context)

    private val _loginError = MutableLiveData<String?>()
    val authError: LiveData<String?> = _loginError

    private val _registeredIds = MutableLiveData<List<String>>()
    val registeredIds: LiveData<List<String>> = _registeredIds

    private val _verifyError = MutableLiveData<String?>()
    val verifyError: LiveData<String?> = _verifyError

    private val _resetPwdError = MutableLiveData<String?>()
    val resetPwdError: LiveData<String?> = _resetPwdError

    init {
        loadUserIds()
    }

    fun loadUserIds() {
        viewModelScope.launch {
            val allUserIds = authRepository.getAllUserIds()
            val claimed = mutableListOf<String>()

            allUserIds.forEach { userId ->
                if (authRepository.isClaimed(userId)) {
                    claimed.add(userId)
                }
            }
            _registeredIds.postValue(claimed)
        }
    }

    suspend fun attemptLogin(userId: String, password: String): Boolean {
        // check if user id exist
        _loginError.postValue(null)
        if (!authRepository.existsUserId(userId)) {
            _loginError.postValue("User ID not found!")
            return false
        }
        // check if the account claimed
        if (!authRepository.isClaimed(userId)) {
            _loginError.postValue("Account not yet registered!")
            return false
        }
        // check if the password correct
        if (!authRepository.verifyPassword(userId, password)) {
            _loginError.postValue("Password incorrect!")
            return false
        }
        authRepository.login(userId, password)
        _loginError.postValue(null)
        return true

    }

    suspend fun attemptVerify(userId: String, phone: String): Boolean {
        _verifyError.postValue(null)
        // check if user id exist
        if (!authRepository.existsUserId(userId)) {
            _verifyError.postValue("User ID not found!")
            return false
        }
        // check if phone number match
        if (!authRepository.checkPhone(userId, phone)) {
            _verifyError.postValue("Phone number does not match!")
            return false
        }
        _verifyError.postValue(null)
        return true

    }

    suspend fun attemptReset(userId: String, password: String): Boolean {
        _resetPwdError.postValue(null)
        // check if user id exist
        if (!authRepository.existsUserId(userId)) {
            _verifyError.postValue("User ID not found!")
            return false
        }
        // check the password strength
        if (password.length < 8 || password.none { it.isUpperCase() } || password.none { it.isLowerCase() } || password.none { it.isDigit() } || password.none { "!@#$%^&*()".contains(it) }
        ) {
            _resetPwdError.postValue("Password must be more than 8 characters, include a capital letter, a special character and a number!")
            return false
        }
        authRepository.resetPassword(userId, password)
        _resetPwdError.postValue(null)
        return true

    }

    fun clearResetError() {
        _resetPwdError.postValue(null)
    }

    fun clearVerifyError() {
        _verifyError.postValue(null)
    }

    fun clearAuthError() {
        _loginError.postValue(null)
    }


    //a view model factory that sets the context for the viewmodel
    //The ViewModelProvider.Factory interface is used to create view models.
    class LoginViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext

        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            LoginViewModel(context) as T
    }

}