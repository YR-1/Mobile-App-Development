package com.nutritrackpro.data.patients

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PatientsViewModel (context: Context) : ViewModel() {

    private val patientsRepository: PatientsRepository =
        PatientsRepository(context)

    init {
        initializeDatabase()
    }

    private fun initializeDatabase() {
        viewModelScope.launch {
            patientsRepository.initializeDatabase()
        }
    }

    //a view model factory that sets the context for the viewmodel
    //The ViewModelProvider.Factory interface is used to create view models.
    class PatientsViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext

        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PatientsViewModel(context) as T
    }
}
