package com.buzuriu.dogapp.viewModels

import android.os.Handler
import android.os.Looper
import com.beastwall.localisation.Localisation
import com.beastwall.localisation.model.City
import com.beastwall.localisation.model.Country
import com.buzuriu.dogapp.adapters.CityAdapter
import com.buzuriu.dogapp.models.CityObj
import com.buzuriu.dogapp.models.CountryObj

class SelectCityViewModel : BaseViewModel() {

    var cityAdapter: CityAdapter? = null
    var selectedCountry: Country? = null
    private var citiesList: ArrayList<CityObj> = ArrayList()

    init {
        cityAdapter = CityAdapter(citiesList, this)
        selectedCountry =
            exchangeInfoService.get<Country>(this::class.java.name)!!
        initCitiesList()
    }


    private fun initCitiesList() {
        Thread {
            var cities = listOf<City>()

            val states =
                selectedCountry!!.states

            for (state in states) {
                cities.plus(state.cities)
            }
            Handler(Looper.getMainLooper()).post {

                for (city in cities) {
                        citiesList.add(CityObj(city, false))

                    }
                cityAdapter!!.notifyDataSetChanged()
            }
        }.start()
    }


    public fun selectCity(cityObj: CityObj) {

    }

    fun close() {

    }

    fun saveCity() {

    }
}