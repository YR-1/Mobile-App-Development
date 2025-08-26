package com.nutritrackpro.data.nutriCoachTips

import android.content.Context
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nutritrackpro.data.AuthManager
import com.nutritrackpro.data.foodIntake.FoodIntake
import com.nutritrackpro.data.foodIntake.FoodIntakesViewModel
import com.nutritrackpro.data.genAI.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NutriCoachTipsViewModel (context: Context) : ViewModel() {

    private val nutriCoachTipsRepository: NutriCoachTipsRepository =
        NutriCoachTipsRepository(context)

    private val _allTips = MutableStateFlow<List<NutriCoachTips>>(emptyList())
    val allTips: StateFlow<List<NutriCoachTips>> = _allTips

    private var _isSaving = mutableStateOf(false)

    fun getAllTips(userId: String) {
        viewModelScope.launch {
            nutriCoachTipsRepository.getAllTips(userId).collect { tips ->
                _allTips.value = tips
            }
        }
    }

    fun saveTip(userId: String, tipText: String) {
//        if (_isSaving.value) {
//            return
//        }
//        _isSaving.value = true
        val tip = NutriCoachTips(
            userId  = userId,
            tip = tipText
        )
        viewModelScope.launch {
            try {
                nutriCoachTipsRepository.saveTip(tip)
            } finally {
                _isSaving.value = false
            }
        }
    }

    //a view model factory that sets the context for the viewmodel
    //The ViewModelProvider.Factory interface is used to create view models.
    class NutriCoachTipsViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext

        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            NutriCoachTipsViewModel(context) as T
    }
}