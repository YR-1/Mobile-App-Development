package com.nutritrackpro.data.fruitAPI

data class FruitModel(
    val name: String,
    val family: String,
    val nutritions: Map<String, Float>,
)
