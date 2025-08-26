package com.nutritrackpro.data.foodIntake

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nutritrackpro.data.AuthManager
import com.nutritrackpro.data.patients.Patient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FoodIntakesViewModel (context: Context) : ViewModel() {

    private val foodIntakesRepository: FoodIntakesRepository =
        FoodIntakesRepository(context)

    private val _intakeLive = MutableStateFlow<FoodIntake?>(null)
    val intakeLive: StateFlow<FoodIntake?> = _intakeLive

    private val _selectedCategories = mutableStateListOf<String>()
    val selectedCategories: List<String> get() = _selectedCategories

    val bestPersona = mutableStateOf("")
    val mealTime = mutableStateOf("00:00")
    val sleepTime = mutableStateOf("00:00")
    val wakeTime = mutableStateOf("00:00")

    fun updateSelection(category: String, isSelected: Boolean) {
        if (isSelected) {
            _selectedCategories.add(category)
        } else {
            _selectedCategories.remove(category)
        }
    }

    fun getIntake(userId: String) {
        viewModelScope.launch {
            foodIntakesRepository.getIntake(userId).collect { intake ->
                _intakeLive.value = intake
            }
        }
    }

    fun loadQuestionnaire(userId: String) {
        viewModelScope.launch {
            foodIntakesRepository.getIntake(userId).collect { intake ->
                _intakeLive.value = intake
                // update UI state if data exists
            intake?.let {
                    updateSelections(intake)
                    bestPersona.value = intake.bestPersona ?: ""
                    mealTime.value = intake.mealTime ?: "00:00"
                    sleepTime.value = intake.sleepTime ?: "00:00"
                    wakeTime.value = intake.wakeTime ?: "00:00"
                }
            }
        }
    }

    private fun updateSelections(intake: FoodIntake) {
        _selectedCategories.clear()
        if (intake.fruits) _selectedCategories.add("Fruits")
        if (intake.vegetables) _selectedCategories.add("Vegetables")
        if (intake.grains) _selectedCategories.add("Grains")
        if (intake.redMeat) _selectedCategories.add("Red Meat")
        if (intake.seafood) _selectedCategories.add("Seafood")
        if (intake.poultry) _selectedCategories.add("Poultry")
        if (intake.fish) _selectedCategories.add("Fish")
        if (intake.eggs) _selectedCategories.add("Eggs")
        if (intake.nutsSeeds) _selectedCategories.add("Nuts/Seeds")
    }



    // need to check is the patient has intake before
    fun saveQuestionnaire(
        userId: String
    ) {
        val intake = FoodIntake(
            userId   = userId,
            fruits      = selectedCategories.contains("Fruits"),
            vegetables  = selectedCategories.contains("Vegetables"),
            grains      = selectedCategories.contains("Grains"),
            redMeat     = selectedCategories.contains("Red Meat"),
            seafood     = selectedCategories.contains("Seafood"),
            poultry     = selectedCategories.contains("Poultry"),
            fish        = selectedCategories.contains("Fish"),
            eggs        = selectedCategories.contains("Eggs"),
            nutsSeeds   = selectedCategories.contains("Nuts/Seeds"),
            bestPersona = bestPersona.value,
            mealTime    = mealTime.value,
            sleepTime   = sleepTime.value,
            wakeTime    = wakeTime.value
        )

        viewModelScope.launch {
            foodIntakesRepository.saveQuestionnaire(intake)
        }
    }

    //a view model factory that sets the context for the viewmodel
    //The ViewModelProvider.Factory interface is used to create view models.
    class FoodIntakesViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext

        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            FoodIntakesViewModel(context) as T
    }
}