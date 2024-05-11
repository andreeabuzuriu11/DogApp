package com.buzuriu.dogapp.models

data class DogPersonality(
    val breed: String,
    val description: String,
    val temperament: String,
    val popularity: String,
    val min_height: String,
    val max_height: String,
    val min_weight: String,
    val max_weight: String,
    val min_expectancy: String,
    val max_expectancy: String,
    val group: String,
    val grooming_frequency_value: String,
    val grooming_frequency_category: String,
    val shedding_value: String,
    val shedding_category: String,
    val energy_level_value: String,
    val energy_level_category: String,
    val trainability_value: String,
    val trainability_category: String,
    val demeanor_value: String,
    val demeanor_category: String
)