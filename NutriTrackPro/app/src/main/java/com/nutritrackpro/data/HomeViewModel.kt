package com.nutritrackpro.data

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nutritrackpro.data.patients.Patient
import com.nutritrackpro.data.patients.PatientsRepository
import com.nutritrackpro.data.patients.PatientsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel (private val context: Context) : ViewModel() {

    private val patientsRepository: PatientsRepository =
        PatientsRepository(context)

    private val _patient = MutableStateFlow<Patient?>(null)
    val patient: StateFlow<Patient?> = _patient

    private val _femaleAvg = mutableStateOf<Float?>(null)
    val femaleAvg: State<Float?> = _femaleAvg

    private val _maleAvg = mutableStateOf<Float?>(null)
    val maleAvg: State<Float?> = _maleAvg

    private val _allPatients = mutableStateOf<List<Patient>>(emptyList())
    val allPatients: State<List<Patient>> = _allPatients

    fun getPatient(userId: String) {
        viewModelScope.launch {
            patientsRepository.observePatient(
                AuthManager.getPatientId()
                    .toString()
            ).collect { patient ->
                _patient.value = patient
            }
        }
    }

    fun getAverageHeifa() {
        viewModelScope.launch {
            _femaleAvg.value = patientsRepository.getFemaleAverageHeifa()
            _maleAvg.value   = patientsRepository.getMaleAverageHeifa()
        }

    }

    fun getPatients() {
        viewModelScope.launch(Dispatchers.IO) {
            val allPatients = patientsRepository.getAllPatients()
            _allPatients.value = allPatients
        }
    }
    //a view model factory that sets the context for the viewmodel
    //The ViewModelProvider.Factory interface is used to create view models.
    class HomeViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext

        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HomeViewModel(context) as T
    }
}
