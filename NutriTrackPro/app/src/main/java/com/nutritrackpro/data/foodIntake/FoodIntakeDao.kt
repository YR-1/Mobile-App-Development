package com.nutritrackpro.data.foodIntake

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.nutritrackpro.data.patients.Patient
import kotlinx.coroutines.flow.Flow

// This interface defines the data access object (DAO) for the FoodIntake entity.
@Dao
interface FoodIntakeDao {
    //suspend is a coroutine function that can be paused and resumed at a later time.
    //suspend is used to indicate that the function will be called from a coroutine.
    /**
     *  Inserts a new foodIntake into the database.
     */
    @Insert
    suspend fun insert(foodIntake: FoodIntake)

    /**
     *  Updates an existing foodIntake in the database.
     */
    @Update
    suspend fun update(foodIntake: FoodIntake)

    /**
     * Retrieves a patient from the database based on their ID.
     */
    @Query("SELECT * FROM food_intake WHERE userId = :userId")
    fun getIntakeById(userId: String): Flow<FoodIntake?>

    @Query("SELECT EXISTS(SELECT * FROM food_intake WHERE userId = :userId)")
    suspend fun isIntakeExist(userId: String): Boolean


}

