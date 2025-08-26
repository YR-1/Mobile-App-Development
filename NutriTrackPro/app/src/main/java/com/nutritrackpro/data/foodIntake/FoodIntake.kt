package com.nutritrackpro.data.foodIntake

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nutritrackpro.data.patients.Patient

/**
 * Represents a food intake questionnaire made by a patient.
 *
 * This data class is annotated with @Entity to mark it as a Room database table.
 * The table name is "food_intake".
 */
@Entity(tableName = "food_intake",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("userId")]
)
data class FoodIntake(
    /**
     * Unique identifier of the patient the food intake belongs to.
     */
    @PrimaryKey
    val userId: String,
    //the food categories user can eat
    /**
     * Whether the patient can consumes fruits
     */
    val fruits: Boolean = false,
    /**
     * Whether the patient can consumes vegetables
     */
    val vegetables: Boolean = false,
    /**
     * Whether the patient can consumes grains
     */
    val grains: Boolean = false,
    /**
     * Whether the patient can consumes redMeat
     */
    val redMeat: Boolean = false,
    /**
     * Whether the patient can consumes seafood
     */
    val seafood: Boolean = false,
    /**
     * Whether the patient can consumes poultry
     */
    val poultry: Boolean = false,
    /**
     * Whether the patient can consumes fish
     */
    val fish: Boolean = false,
    /**
     * Whether the patient can consumes eggs
     */
    val eggs: Boolean = false,
    /**
     * Whether the patient can consumes nutsSeeds
     */
    val nutsSeeds: Boolean = false,
    /**
     * The persona best fits the patient
     */
    val bestPersona: String? = null,
    /**
     * The biggest meal time of the patient
     */
    val mealTime: String? = null,
    /**
     * The sleep time of the patient
     */
    val sleepTime: String? = null,
    /**
     * The wake time of the patient
     */
    val wakeTime: String? = null,
)
