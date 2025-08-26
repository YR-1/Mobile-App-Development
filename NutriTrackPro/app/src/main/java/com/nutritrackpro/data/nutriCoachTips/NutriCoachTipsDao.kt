package com.nutritrackpro.data.nutriCoachTips

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// This interface defines the data access object (DAO) for the NutriCoachTips entity.
@Dao
interface NutriCoachTipsDao {
    //suspend is a coroutine function that can be paused and resumed at a later time.
    //suspend is used to indicate that the function will be called from a coroutine.
    /**
     *  Inserts a new NutriCoachTip into the database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tip: NutriCoachTips)

    /**
     * Retrieves a patient from the database based on their ID.
     */
    @Query("SELECT * FROM nutri_coach_tips WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllTips(userId: String): Flow<List<NutriCoachTips>>

}