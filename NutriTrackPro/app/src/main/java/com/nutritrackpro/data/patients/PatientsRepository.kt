package com.nutritrackpro.data.patients

import android.content.Context
import android.util.Log
import com.nutritrackpro.data.ClinicDatabase
import java.io.BufferedReader
import java.io.InputStreamReader
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import com.nutritrackpro.data.AuthManager
import com.nutritrackpro.data.foodIntake.FoodIntake
import kotlinx.coroutines.flow.Flow


class PatientsRepository (val context: Context) {
    // Create an instance of the Patient DAO
    private val patientDao =
        ClinicDatabase.getDatabase(context).patientDao()

    private val sharedPref = context.getSharedPreferences("nutri_sp", Context.MODE_PRIVATE)

    // observe patient data over time
    fun observePatient(userId: String): Flow<Patient?> {
        return patientDao.getPatientById(userId)
    }

    suspend fun getPatient(userId: String): Patient? {
        return patientDao.getPatient(userId)
    }

    suspend fun initializeDatabase() {
        // if the first_run if false, load the csv
        if (!(sharedPref.getBoolean("first_run", false))) {
            loadCsvData()
            sharedPref.edit() { putBoolean("first_run", true) }
        }
    }

    private suspend fun loadCsvData() {
        try {
            context.assets.open("test.csv").use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    val header = reader.readLine().split(",")
                    val patients = mutableListOf<Patient>()
                    reader.lineSequence().forEach { line ->
                        val columns = line.split(",")
                        val patient = (parseCsv(columns, header))
                        patients.add(patient)
                    }
                    patientDao.insertAll(patients)
                }
            }
        } catch (e: Exception) {
            Log.e("CSV", "Error reading CSV", e)
        }
    }

    private fun parseCsv(columns: List<String>, header: List<String>): Patient {
        val scores = mutableMapOf<String, Float>()
        val gender = columns[2]

        header.forEachIndexed { index, name ->
            if (name == "Fruitservesize"){
                scores[name] = columns[index].toFloat()
            }
            if (name == "Fruitvariationsscore"){
                scores[name] = columns[index].toFloat()
            }
            if (name.contains("HEIFA") && name.endsWith(gender)) {
                val nutriName = name.replace("HEIFA", "")
                    .replace("score", "")
                    .replace(gender, "")
                    .lowercase()
                scores[nutriName] = columns[index].toFloat()
            }
        }

        return Patient(
            userId = columns[1],
            phoneNumber = columns[0],
            sex = gender,
            totalScore = scores["total"]!!,
            discretionaryScore = scores["discretionary"]!!,
            vegetablesScore = scores["vegetables"]!!,
            fruitsScore = scores["fruit"]!!,
            fruitServeSize = scores["Fruitservesize"]!!,
            fruitVariationsScore = scores["Fruitvariationsscore"]!!,
            grainsScore = scores["grainsandcereals"]!!,
            wholeGrainsScore = scores["wholegrains"]!!,
            meatScore = scores["meatandalternatives"]!!,
            dairyScore = scores["dairyandalternatives"]!!,
            sodiumScore = scores["sodium"]!!,
            alcoholScore = scores["alcohol"]!!,
            waterScore = scores["water"]!!,
            sugarScore = scores["sugar"]!!,
            saturatedFatScore = scores["saturatedfat"]!!,
            unsaturatedFatScore = scores["unsaturatedfat"]!!
        )
    }

    fun getAllPatients(): List<Patient> = patientDao.getAllPatients()

    suspend fun getFemaleAverageHeifa(): Float {
        return patientDao.getFemaleAverageHeifa()
    }

    suspend fun getMaleAverageHeifa(): Float {
        return patientDao.getMaleAverageHeifa()
    }



}