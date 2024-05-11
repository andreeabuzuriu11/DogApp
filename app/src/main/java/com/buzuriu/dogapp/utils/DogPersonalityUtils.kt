package com.buzuriu.dogapp.utils

import com.buzuriu.dogapp.models.DogPersonality
import com.buzuriu.dogapp.models.DogPersonalityTraitObj

class DogPersonalityUtils {
    companion object {

        private const val breed = "Breed"
        private const val description = "Description"
        private const val temperament = "Temperament"
        private const val popularity = "Popularity"
        private const val min_height = "Minimum height"
        private const val max_height = "Maximum height"
        private const val min_weight = "Minimum weight"
        private const val max_weight = "Maximum height"
        private const val min_expectancy = "Minimum expectancy"
        private const val max_expectancy = "Maximum expectancy"
        private const val group = "Group"
        private const val grooming_frequency_value = "Grooming Frequency Value"
        private const val grooming_frequency_category = "Grooming Frequency Category"
        private const val shedding_value = "Shedding Value"
        private const val shedding_category = "Shedding Category"
        private const val energy_level_value = "Energy Level Value"
        private const val energy_level_category = "Energy Level Category"
        private const val trainability_value = "Trainability Value"
        private const val trainability_category = "Trainability Category"
        private const val demeanor_value = "Demeanor Value"
        private const val demeanor_category = "Demeanor Category"


        fun getDogPersonalityTraitListFromDogPersonality(dogPersonality: DogPersonality): ArrayList<DogPersonalityTraitObj> {

            val finalList = kotlin.collections.ArrayList<DogPersonalityTraitObj>()

            finalList.add(DogPersonalityTraitObj(breed, dogPersonality.breed))
            finalList.add(DogPersonalityTraitObj(description, dogPersonality.description))
            finalList.add(DogPersonalityTraitObj(temperament, dogPersonality.temperament))
            finalList.add(DogPersonalityTraitObj(popularity, dogPersonality.popularity))
            finalList.add(DogPersonalityTraitObj(min_height, dogPersonality.min_height))
            finalList.add(DogPersonalityTraitObj(max_height, dogPersonality.max_height))
            finalList.add(DogPersonalityTraitObj(min_weight, dogPersonality.min_weight))
            finalList.add(DogPersonalityTraitObj(max_weight, dogPersonality.max_weight))
            finalList.add(DogPersonalityTraitObj(min_expectancy, dogPersonality.min_expectancy))
            finalList.add(DogPersonalityTraitObj(max_expectancy, dogPersonality.max_expectancy))
            finalList.add(DogPersonalityTraitObj(group, dogPersonality.group))
            finalList.add(DogPersonalityTraitObj(grooming_frequency_value, dogPersonality.grooming_frequency_value))
            finalList.add(DogPersonalityTraitObj(grooming_frequency_category, dogPersonality.grooming_frequency_category))
            finalList.add(DogPersonalityTraitObj(shedding_value, dogPersonality.shedding_value))
            finalList.add(DogPersonalityTraitObj(shedding_category, dogPersonality.shedding_category))
            finalList.add(DogPersonalityTraitObj(energy_level_value, dogPersonality.energy_level_value))
            finalList.add(DogPersonalityTraitObj(energy_level_category, dogPersonality.energy_level_category))
            finalList.add(DogPersonalityTraitObj(trainability_value, dogPersonality.trainability_value))
            finalList.add(DogPersonalityTraitObj(trainability_category, dogPersonality.trainability_category))
            finalList.add(DogPersonalityTraitObj(demeanor_value, dogPersonality.demeanor_value))
            finalList.add(DogPersonalityTraitObj(demeanor_category, dogPersonality.demeanor_category))

            return finalList
        }

    }
}