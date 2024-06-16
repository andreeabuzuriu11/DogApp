package com.buzuriu.dogapp.viewModels

import com.beastwall.localisation.model.Country
import com.buzuriu.dogapp.adapters.EditCityAdapter
import com.buzuriu.dogapp.models.CityObj
import com.buzuriu.dogapp.models.LocationObj
import java.util.*

class EditCityViewModel : BaseViewModel() {

    var cityAdapter: EditCityAdapter? = null
    var selectedCountry: Country? = null
    var selectedCity: CityObj? = null
    var locationObj: LocationObj? = null
    var isFromEditAccount: Boolean = false
    private var citiesList: ArrayList<CityObj> = ArrayList()

    init {
        cityAdapter = EditCityAdapter(citiesList, this)
        locationObj =
            exchangeInfoService.get<LocationObj>(this::class.java.name)!!
        var cities = locationObj!!.state!!.cities
        for (city in cities) {
            citiesList.add(CityObj(city, false))
        }
        cityAdapter!!.notifyDataSetChanged()
    }


    public fun selectCity(cityObj: CityObj) {
        unselectPreviousCity()
        cityObj.isSelected = true
        selectedCity = cityObj

        cityAdapter?.notifyItemChanged(cityAdapter?.citiesList!!.indexOf(cityObj))
    }

    fun close() {
        navigationService.closeFragment()
    }

    fun saveCity() {
        if (selectedCity == null) {
            snackMessageService.displaySnackBar("Please select a city")
            return
        }
        locationObj!!.city = selectedCity!!.city

        exchangeInfoService.put(EditAccountViewModel::class.qualifiedName!!, locationObj!!)
        close()
    }

    fun searchByName(searchedString: String) {
        val auxSearchedCities = java.util.ArrayList<CityObj>()
        if (citiesList.isNotEmpty()) {
            for (item in citiesList) {
                val mySearchedString = searchedString.lowercase(Locale.ROOT)
                val itemString = item.city!!.name?.lowercase(Locale.ROOT)

                if (itemString!!.contains(mySearchedString) || mySearchedString.isEmpty()) {
                    auxSearchedCities.add(item)
                }
            }
        }
        cityAdapter!!.filterList(auxSearchedCities)
    }

    private fun unselectPreviousCity() {
        for (city in citiesList) {
            if (city.isSelected!!) {
                city.isSelected = false
                cityAdapter?.notifyItemChanged(cityAdapter?.citiesList!!.indexOf(city))
                return
            }
        }
    }


}