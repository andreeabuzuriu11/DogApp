package com.buzuriu.dogapp.services

import com.buzuriu.dogapp.models.DogPersonality
import com.buzuriu.dogapp.utils.LocalDBItems
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.InputStreamReader

interface ICsvDataService {
    fun readDogPersonalityCsv()
}

class CsvDataService(
    private val activityService: ICurrentActivityService,
    private val localDatabaseService: ILocalDatabaseService
) : ICsvDataService {
    override fun readDogPersonalityCsv() {
        var dogPersonalityList = ArrayList<DogPersonality>()

        val inputStream =
            activityService.activity!!.applicationContext.assets.open("akc-data-latest.csv")
        val reader = InputStreamReader(inputStream)

        val csvParser = CSVParser(
            reader, CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withIgnoreHeaderCase()
                .withTrim()
        );

        for (csvRecord in csvParser) {
            val breed = csvRecord.get(0);
            val description = csvRecord.get(1);
            val temperament = csvRecord.get(2);
            val popularity = csvRecord.get(3);
            val min_height = csvRecord.get(4);
            val max_height = csvRecord.get(5);
            val min_weight = csvRecord.get(6);
            val max_weight = csvRecord.get(7);
            val min_expectancy = csvRecord.get(8);
            val max_expectancy = csvRecord.get(9);
            val group = csvRecord.get(10);
            val grooming_frequency_value = csvRecord.get(11);
            val grooming_frequency_category = csvRecord.get(12);
            val shedding_value = csvRecord.get(13);
            val shedding_category = csvRecord.get(14);
            val energy_level_value = csvRecord.get(15);
            val energy_level_category = csvRecord.get(16);
            val trainability_value = csvRecord.get(17);
            val trainability_category = csvRecord.get(18);
            val demeanor_value = csvRecord.get(19);
            val demeanor_category = csvRecord.get(20);

            dogPersonalityList.add(
                DogPersonality(
                    breed,
                    description,
                    temperament,
                    popularity,
                    min_height,
                    max_height,
                    min_weight,
                    max_weight,
                    min_expectancy,
                    max_expectancy,
                    group,
                    grooming_frequency_value,
                    grooming_frequency_category,
                    shedding_value,
                    shedding_category,
                    energy_level_value,
                    energy_level_category,
                    trainability_value,
                    trainability_category,
                    demeanor_value,
                    demeanor_category
                )
            )
        }
        localDatabaseService.add(LocalDBItems.dogPersonalityList, dogPersonalityList)
    }
}