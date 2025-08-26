package com.nutritrackpro.data.authentication

import android.content.Context
import android.util.Base64
import com.nutritrackpro.data.AuthManager
import com.nutritrackpro.data.ClinicDatabase
import com.nutritrackpro.data.patients.Patient
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest

class AuthRepository (val context: Context) {
    //create an instance of the Patient DAO
    private val patientDao =
        ClinicDatabase.getDatabase(context).patientDao()

    // observe patient data over time
    fun observePatient(userId: String): Flow<Patient?> {
        return patientDao.getPatientById(userId)
    }

    suspend fun getPatient(userId: String): Patient? {
        return patientDao.getPatient(userId)
    }

    suspend fun getAllUserIds(): List<String> {
        return patientDao.getAllUserIds()
    }

    suspend fun claimAccount(userId: String, name: String, password: String) {
        val patient = patientDao.getPatient(userId)
        if (patient != null) {
            patient.name = name
            patient.password = hashPassword(password)
            patientDao.update(patient)
        }
    }

    suspend fun resetPassword(userId: String, password: String) {
        val patient = patientDao.getPatient(userId)
        if (patient != null) {
            patient.password = hashPassword(password)
            patientDao.update(patient)
        }
    }

    // return true if the user id exist
    suspend fun existsUserId(userId: String): Boolean {
        return (patientDao.getPatient(userId) != null)
    }

    // true if the phone number correct
    suspend fun checkPhone(userId: String, phone: String): Boolean {
        val patient = patientDao.getPatient(userId)
        return patient?.phoneNumber?.filter { it.isDigit() }  == phone.filter { it.isDigit() }
    }

    // true if the password correct
    suspend fun verifyPassword(userId: String, password: String): Boolean {
        val patient = patientDao.getPatient(userId)
        return patient?.password == hashPassword(password)
    }

    suspend fun isClaimed(userId: String): Boolean {
        val patient = patientDao.getPatient(userId)
        if (patient != null) {
            return (patient.password != null && patient.name != null)
        }
        return false
    }

    suspend fun login(userId: String, password: String): Boolean {
        //val hashedPassword = hashPassword(password)
        val patient = patientDao.getPatient(userId)
        if (patient != null && verifyPassword(userId, password)) {
            AuthManager.login(context, userId)
            return true
        }
        return false
    }

    private fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val input = password.toByteArray(Charsets.UTF_8)
        val bytes = md.digest(input)
        return Base64.encodeToString(
            bytes,
            Base64.NO_WRAP
        ) //tells the encoder not to add line breaks (\n) in the output string
    }
}