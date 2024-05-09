package com.buzuriu.dogapp.viewModels

import android.os.Handler
import android.os.Looper
import com.beastwall.localisation.Localisation
import com.buzuriu.dogapp.adapters.CityAdapter
import com.buzuriu.dogapp.models.CityObj


class SelectCityViewModel : BaseViewModel() {

    var cityAdapter: CityAdapter? = null
    private var citiesList: ArrayList<CityObj> = ArrayList()

    init {
        cityAdapter = CityAdapter(citiesList, this)
        initCitiesList()
    }

    fun saveCity() {

    }

    fun selectCity(cityObj: CityObj) {

    }

    private fun initCitiesList() {
        Thread {
            val countries =
                Localisation.getAllCountriesStatesAndCities()
            Handler(Looper.getMainLooper()).post {
                for (country in countries) {
                    citiesList.add(CityObj(country.name, false))
                }
                cityAdapter!!.notifyDataSetChanged()
            }
        }.start()
    }


    fun close() {
        navigationService.closeCurrentActivity()
    }
}