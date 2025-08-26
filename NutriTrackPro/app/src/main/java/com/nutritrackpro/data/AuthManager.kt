package com.nutritrackpro.data

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit

object AuthManager{
    val _userId: MutableState<String?> = mutableStateOf(null)
    val _isLoggedIn: MutableState<Boolean> = mutableStateOf(false)
    val _isClinicianLoggedIn: MutableState<Boolean> = mutableStateOf(false)

    fun login(context: Context, userId: String) {
        val sharedPref = context.getSharedPreferences("nutri_sp", Context.MODE_PRIVATE)
        sharedPref.edit() {
            putBoolean("isLoggedIn", true)
                .putString("currentUserId", userId)
        }
        _userId.value = userId
        _isLoggedIn.value = true
    }

    fun clinicianLogin() {
        _isClinicianLoggedIn.value = true
    }

    fun logout(context: Context) {
        val sharedPref = context.getSharedPreferences("nutri_sp", Context.MODE_PRIVATE)
        sharedPref.edit() {
            putBoolean("isLoggedIn", false)
                .remove("currentUserId")
        }
        _userId.value = null
        _isLoggedIn.value = false
    }

    fun clinicianLogout() {
        _isClinicianLoggedIn.value = false
    }

    fun getPatientId(): String? {
        return _userId.value
    }

    fun isLoggedIn(): Boolean {
        return _isLoggedIn.value
    }

    fun isClinicianLoggedIn(): Boolean {
        return _isClinicianLoggedIn.value
    }
}

