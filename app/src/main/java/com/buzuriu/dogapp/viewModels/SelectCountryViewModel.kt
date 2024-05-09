package com.buzuriu.dogapp.viewModels

import android.os.Handler
import android.os.Looper
import com.beastwall.localisation.Localisation
import com.buzuriu.dogapp.adapters.CountryAdapter
import com.buzuriu.dogapp.models.CountryObj


class SelectCountryViewModel : BaseViewModel() {

    var countryAdapter: CountryAdapter? = null
    private var countriesList: ArrayList<CountryObj> = ArrayList()

    init {
        countryAdapter = CountryAdapter(countriesList, this)
        initCountriesList()
    }

    fun saveCity() {

    }

    fun selectCountry(countryObj: CountryObj) {

    }

    private fun initCountriesList() {
        Thread {
            val countries =
                Localisation.getAllCountriesStatesAndCities()
            Handler(Looper.getMainLooper()).post {
                for (country in countries) {
                    countriesList.add(CountryObj(country.name, false))
                }
                countryAdapter!!.notifyDataSetChanged()
            }
        }.start()
    }


    fun close() {
        navigationService.closeCurrentActivity()
    }
}