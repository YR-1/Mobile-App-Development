package com.nutritrackpro.data.authentication

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RegisterViewModel(context: Context) : ViewModel() {

    private val authRepository: AuthRepository =
        AuthRepository(context)

    private val _registerError = MutableLiveData<String?>()
    val authError: LiveData<String?> = _registerError

    private val _unclaimedUserIds = MutableLiveData<List<String>>()
    val unclaimedUserIds: LiveData<List<String>> = _unclaimedUserIds

    init {
        loadUserIds()
    }

    fun loadUserIds() {
        viewModelScope.launch {
            val allUserIds = authRepository.getAllUserIds()
            val unclaimed = mutableListOf<String>()

            allUserIds.forEach { userId ->
                if (!authRepository.isClaimed(userId)) {
                    unclaimed.add(userId)
                }
            }
            _unclaimedUserIds.postValue(unclaimed)
        }
    }

    suspend fun attemptRegister(userId: String, phone: String, name: String, password: String): Boolean {
        _registerError.postValue(null)
        // check if user id exist
        if (!authRepository.existsUserId(userId)) {
            _registerError.postValue("User ID not found!")
            return false
        }
        // check if phone number match
        if (!authRepository.checkPhone(userId, phone)) {
            _registerError.postValue("Phone number does not match!")
            return false
        }
        // check if the account claimed
        if (authRepository.isClaimed(userId)) {
        _registerError.postValue("This account has already been claimed!")
        return false
        }
        // check the password strength
        if (password.length < 8 || password.none { it.isUpperCase() } || password.none { it.isLowerCase() } || password.none { it.isDigit() } || password.none { "!@#$%^&*()".contains(it) }
        ) {
            _registerError.postValue("Password must be more than 8 characters, include a capital letter, a special character and a number!")
            return false
        }
        authRepository.claimAccount(userId, name, password)
        _registerError.postValue(null)
        return true

    }



    fun clearAuthError() {
        _registerError.postValue(null)
    }


    //a view model factory that sets the context for the viewmodel
    //The ViewModelProvider.Factory interface is used to create view models.
    class RegisterViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext

        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            RegisterViewModel(context) as T
    }

}