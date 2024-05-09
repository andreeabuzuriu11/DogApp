package com.buzuriu.dogapp.viewModels

import com.buzuriu.dogapp.adapters.CityAdapter
import com.buzuriu.dogapp.models.CityObj
import com.buzuriu.dogapp.models.CountryObj

class SelectCityViewModel : BaseViewModel() {

    var cityAdapter : CityAdapter?= null
    var selectedCountry : CountryObj? = null
    private var citiesList: ArrayList<CityObj> = ArrayList()

    init {
        cityAdapter = CityAdapter(citiesList, this)
        selectedCountry =
            exchangeInfoService.get<CountryObj>(this::class.java.name)!!
    }
    public fun selectCity(cityObj: CityObj) {

    }

    fun close() {

    }

    fun saveCity()
    {

    }
}