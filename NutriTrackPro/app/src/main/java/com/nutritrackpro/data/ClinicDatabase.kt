package com.nutritrackpro.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nutritrackpro.data.foodIntake.FoodIntake
import com.nutritrackpro.data.foodIntake.FoodIntakeDao
import com.nutritrackpro.data.nutriCoachTips.NutriCoachTips
import com.nutritrackpro.data.nutriCoachTips.NutriCoachTipsDao
import com.nutritrackpro.data.patients.Patient
import com.nutritrackpro.data.patients.PatientDao

/**
 * This is the database class for the application. It is a Room database.
 * It contains two entity: [Patient, FoodIntake, NutriCoachTips].
 * The version is 1 and exportSchema is false.
 */
@Database(entities = [Patient::class, FoodIntake::class, NutriCoachTips::class], version = 1, exportSchema = false)
// this is a room database

abstract class ClinicDatabase: RoomDatabase() {

    /**
     * Provides access to the PatientDao interface for performing
     * database operations on Patient entities.
     * @return PatientDao instance.
     */
    abstract fun patientDao(): PatientDao
    /**
     * Provides access to the FoodIntakeDao interface for performing
     * database operations on FoodIntake entities.
     * @return FoodIntakeDao instance.
     */
    abstract fun foodIntakeDao(): FoodIntakeDao
    /**
     * Provides access to the NutriCoachTipsDao interface for performing
     * database operations on NutriCoachTips entities.
     * @return NutriCoachTipsDao instance.
     */
    abstract fun nutriCoachTipsDao(): NutriCoachTipsDao

    companion object {
        /**
         * This is a volatile variable that holds the database instance.
         * It is volatile so that it is immediately visible to all threads.
         */
        @Volatile
        private var Instance: ClinicDatabase? = null

        /**
         * Returns the database instance.
         * If the instance is null, it creates a new database instance.
         * @param context The context of the application.
         * @return The singleton instance of ClinicDatabase.
         */
        fun getDatabase(context: Context): ClinicDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            //synchronized means that only one thread can access this code at a time.
            //clinic_database is the name of the database.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ClinicDatabase::class.java, "clinic_database")
                    .fallbackToDestructiveMigration().build().also { Instance = it }
            }
        }
    }
}





