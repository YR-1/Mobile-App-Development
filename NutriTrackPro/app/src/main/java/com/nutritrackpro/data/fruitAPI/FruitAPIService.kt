package com.nutritrackpro.data.fruitAPI

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface FruitAPIService {
    // Interface for defining the API endpoints.
    // Endpoint to fetch a list of posts.
    @GET("api/fruit/{name}")
    suspend fun getDetails(@Path("name") name: String,): Response<FruitModel>
    //https://www.fruityvice.com/api/fruit/banana

    companion object {

        var BASE_URL = "https://www.fruityvice.com/"

        fun create(): FruitAPIService {

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(FruitAPIService::class.java)

        }
    }
}