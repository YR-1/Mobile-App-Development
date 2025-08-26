package com.nutritrackpro.data.fruitAPI

class FruitAPIRepository {
    private val apiService = FruitAPIService.create()

    suspend fun getDetails(name:String): FruitModel? {
        val response = apiService.getDetails(name)
        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

}