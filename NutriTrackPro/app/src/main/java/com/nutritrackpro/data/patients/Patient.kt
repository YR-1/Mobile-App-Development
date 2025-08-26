package com.nutritrackpro.data.patients

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a patient entity in the database.
 *
 * This data class is annotated with `@Entity` to
 * indicate that it represents a table in the database.
 * The `tableName` property specifies the name
 * of the table as "patient".
 */
@Entity(tableName = "patients")
data class Patient(
    /**
     * Unique identifier for the patient.
     */
    @PrimaryKey
    val userId: String,
    /**
     * The phone number of the patient
     * */
    val phoneNumber: String,
    /**
     * The name of the patient.
     */
    var name: String? = null,
    /**
     * The password of the patient.
     */
    var password: String? = null,
    /**
     * The sex of the patient.
     */
    val sex: String,
    /**
     * The total HEIFA score of the patient.
     */
    val totalScore: Float,
    /**
     * The discretionary HEIFA score of the patient.
     */
    val discretionaryScore: Float,
    /**
     * The vegetables HEIFA score of the patient.
     */
    val vegetablesScore: Float,
    /**
     * The fruits HEIFA score of the patient.
     */
    val fruitsScore: Float,
    /**
     * The fruits serve size of the patient.
     */
    val fruitServeSize: Float,
    /**
     * The fruits Variations score of the patient.
     */
    val fruitVariationsScore: Float,
    /**
     * The grains HEIFA score of the patient.
     */
    val grainsScore: Float,
    /**
     * The whole grains HEIFA score of the patient.
     */
    val wholeGrainsScore: Float,
    /**
     * The meat HEIFA score of the patient.
     */
    val meatScore: Float,
    /**
     * The dairy HEIFA score of the patient.
     */
    val dairyScore: Float,
    /**
     * The sodium HEIFA score of the patient.
     */
    val sodiumScore: Float,
    /**
     * The alcohol HEIFA score of the patient.
     */
    val alcoholScore: Float,
    /**
     * The water HEIFA score of the patient.
     */
    val waterScore: Float,
    /**
     * The sugar HEIFA score of the patient.
     */
    val sugarScore: Float,
    /**
     * The saturated fat HEIFA score of the patient.
     */
    val saturatedFatScore: Float,
    /**
     * The unsaturated fat HEIFA score of the patient.
     */
    val unsaturatedFatScore: Float

)



