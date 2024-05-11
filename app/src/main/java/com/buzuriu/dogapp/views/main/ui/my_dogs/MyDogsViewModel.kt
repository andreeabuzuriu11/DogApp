package com.buzuriu.dogapp.views.main.ui.my_dogs

import android.R
import android.annotation.SuppressLint
import android.content.res.Resources
import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.adapters.DogAdapter
import com.buzuriu.dogapp.listeners.IClickListener
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.models.DogPersonality
import com.buzuriu.dogapp.utils.LocalDBItems
import com.buzuriu.dogapp.viewModels.BaseViewModel
import com.buzuriu.dogapp.viewModels.DogDetailViewModel
import com.buzuriu.dogapp.views.AccountDetailActivity
import com.buzuriu.dogapp.views.AddDogActivity
import com.buzuriu.dogapp.views.DogDetailActivity
import com.buzuriu.dogapp.views.auth.LoginActivity
import com.opencsv.CSVReader
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.*


@SuppressLint("NotifyDataSetChanged")
class MyDogsViewModel : BaseViewModel() {

    private var dogsList: ArrayList<DogObj> = ArrayList()
    var dogAdapter: DogAdapter?
    var doesUserHaveAnyDog = MutableLiveData(false)

    init {
        val dogsFromLocalDB =
            localDatabaseService.get<ArrayList<DogObj>>(LocalDBItems.localDogsList)
        if (dogsFromLocalDB != null && dogsFromLocalDB.size > 0) {
            dogsList.addAll(dogsFromLocalDB)
            doesUserHaveAnyDog.value = true
        } else
            doesUserHaveAnyDog.value = false

        dogAdapter = DogAdapter(dogsList, ::selectedDog)
        dogAdapter!!.notifyDataSetChanged()

        // read the csv
        readCsv()
    }

    fun readCsv() {
        var dogPersonalityList = ArrayList<DogPersonality>()
//        val bufferedReader = BufferedReader(FileReader(File("assets/akc-data-latest.csv")))
//        val csvParser = CSVParser(bufferedReader, CSVFormat.DEFAULT);
//        var bufferedReader =
//            activityService.activity!!.applicationContext.assets.open("akc-data-latest.csv")
//                .bufferedReader()
//
//        val csvParser = CSVParser(bufferedReader, CSVFormat.DEFAULT);

        val result: MutableList<Array<String>> = mutableListOf()
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
            var popularity = csvRecord.get(3);
            var min_height = csvRecord.get(4);
            var max_height = csvRecord.get(5);
            var min_weight = csvRecord.get(6);
            var max_weight = csvRecord.get(7);
            var min_expectancy = csvRecord.get(8);
            var max_expectancy = csvRecord.get(9);
            var group = csvRecord.get(10);
            var grooming_frequency_value = csvRecord.get(11);
            var grooming_frequency_category = csvRecord.get(12);
            var shedding_value = csvRecord.get(13);
            var shedding_category = csvRecord.get(14);
            var energy_level_value = csvRecord.get(15);
            var energy_level_category = csvRecord.get(16);
            var trainability_value = csvRecord.get(17);
            var trainability_category = csvRecord.get(18);
            var demeanor_value = csvRecord.get(19);
            var demeanor_category = csvRecord.get(20);

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



    }

    override fun onResume() {
        super.onResume()
        val isRefreshListNeeded: Boolean? =
            exchangeInfoService.get<Boolean>(this::class.qualifiedName!!)
        val dogsFromLocalDB =
            localDatabaseService.get<ArrayList<DogObj>>(LocalDBItems.localDogsList)
        if (isRefreshListNeeded != null && isRefreshListNeeded) {
            dogsList.clear()

            if (dogsFromLocalDB != null && dogsFromLocalDB.size > 0) {
                dogsList.addAll(dogsFromLocalDB)
                doesUserHaveAnyDog.value = true
                dogAdapter!!.notifyDataSetChanged()
            } else
                doesUserHaveAnyDog.value = false

        }

    }

    private fun selectedDog(dogObj: DogObj) {
        exchangeInfoService.put(DogDetailViewModel::class.java.name, dogObj)
        navigationService.navigateToActivity(DogDetailActivity::class.java, false)
    }

    fun addDog() {
        navigationService.navigateToActivity(AddDogActivity::class.java, false)
    }

    fun goToAccountDetails() {
        navigationService.navigateToActivity(AccountDetailActivity::class.java, false)
    }

    fun logout() {
        alertMessageService.displayAlertDialog(
            "Logout?",
            "Are you sure you want to do that?",
            "Logout",
            object :
                IClickListener {
                override fun clicked() {
                    localDatabaseService.clear()
                    navigationService.navigateToActivity(LoginActivity::class.java, true)
                    firebaseAuthService.logout()
                }
            })
    }

}