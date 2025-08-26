package com.nutritrackpro.data.nutriCoachTips

import android.content.Context
import com.nutritrackpro.data.ClinicDatabase
import kotlinx.coroutines.flow.Flow

class NutriCoachTipsRepository (private val context: Context) {
    // Create an instance of the FoodIntake DAO
    private val nutriCoachTipsDao =
        ClinicDatabase.getDatabase(context).nutriCoachTipsDao()

    suspend fun saveTip(tip: NutriCoachTips) {
        nutriCoachTipsDao.insert(tip)
    }

    fun getAllTips(userId: String): Flow<List<NutriCoachTips>> {
        return nutriCoachTipsDao.getAllTips(userId)
    }

}