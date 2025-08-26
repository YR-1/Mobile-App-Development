package com.nutritrackpro.data.foodIntake

import android.content.Context
import androidx.lifecycle.LiveData
import com.nutritrackpro.data.ClinicDatabase
import com.nutritrackpro.data.patients.Patient
import kotlinx.coroutines.flow.Flow

class FoodIntakesRepository (private val context: Context) {
    // Create an instance of the FoodIntake DAO
    private val foodIntakeDao =
        ClinicDatabase.getDatabase(context).foodIntakeDao()

    suspend fun saveQuestionnaire(intake: FoodIntake) {
        if (foodIntakeDao.isIntakeExist(intake.userId)) {
            foodIntakeDao.update(intake)
        }else{
            foodIntakeDao.insert(intake)
        }
    }

    // function to get intake from the database
    fun getIntake(userId: String): Flow<FoodIntake?> {
        return foodIntakeDao.getIntakeById(userId)
    }


}