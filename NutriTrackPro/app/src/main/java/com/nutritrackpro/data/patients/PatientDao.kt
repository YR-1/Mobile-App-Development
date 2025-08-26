package com.nutritrackpro.data.patients

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// This interface defines the data access object (DAO) for the Patient entity.
@Dao
interface PatientDao {
    //suspend is a coroutine function that can be paused and resumed at a later time.
    //suspend is used to indicate that the function will be called from a coroutine.
    /**
     * Insert a new patient into the database
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(patients: List<Patient>)

    /**
     * Updates an existing patient in the database.
     */
    @Update
    suspend fun update(patient: Patient)

    /**
     * Retrieves a patient from the database based on their ID.
     */
    @Query("SELECT * FROM patients WHERE userId = :patientId")
    suspend fun getPatient(patientId: String): Patient?

    /**
     * Retrieves a patient from the database based on their ID.
     */
    @Query("SELECT * FROM patients WHERE userId = :patientId")
    fun getPatientById(patientId: String): Flow<Patient?>

    /**
     * Retrieves all patients from the database.
     */
    @Query("SELECT * FROM patients")
    fun getAllPatients(): List<Patient>

    @Query("SELECT userId FROM patients")
    suspend fun getAllUserIds(): List<String>

    @Query("SELECT AVG(totalScore) FROM patients WHERE sex = 'Female'")
    suspend fun getFemaleAverageHeifa(): Float

    @Query("SELECT AVG(totalScore) FROM patients WHERE sex = 'Male'")
    suspend fun getMaleAverageHeifa(): Float

}

