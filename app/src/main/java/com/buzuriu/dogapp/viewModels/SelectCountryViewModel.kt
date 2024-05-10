package com.buzuriu.dogapp.viewModels

import android.os.Handler
import android.os.Looper
import com.beastwall.localisation.Localisation
import com.buzuriu.dogapp.adapters.CountryAdapter
import com.buzuriu.dogapp.models.BreedObj
import com.buzuriu.dogapp.models.CountryObj
import com.buzuriu.dogapp.utils.LocalDBItems
import com.buzuriu.dogapp.views.SelectCityFragment
import com.buzuriu.dogapp.views.SelectStateFragment
import com.buzuriu.dogapp.views.main.ui.OverlayActivity
import java.util.*
import kotlin.collections.ArrayList


class SelectCountryViewModel : BaseViewModel() {

    var selectedCountry: CountryObj? = null
    var countryAdapter: CountryAdapter? = null
    private var countriesList: ArrayList<CountryObj> = ArrayList()

    init {
        countryAdapter = CountryAdapter(countriesList, this)
        initCountriesList()
    }

    fun saveCountry() {

    }

    fun selectCountry(countryObj: CountryObj) {
        unselectPreviousCountry()
        countryObj.isSelected = true
        selectedCountry = countryObj

        countryAdapter?.notifyItemChanged(countryAdapter?.countriesList!!.indexOf(countryObj))

        if (countryObj.country!!.states.size != 0) {
            // has more states so navigate to states page
            navigationService.showOverlay(
                OverlayActivity::class.java,
                false,
                LocalDBItems.fragmentName,
                SelectStateFragment::class.qualifiedName
            )

            if (selectedCountry != null)
                exchangeInfoService.put(
                    SelectStateViewModel::class.qualifiedName!!,
                    selectedCountry!!.country!!.states
                )

        } else {
//            todo navigate directly to cities
//            navigationService.showOverlay(
//                OverlayActivity::class.java,
//                false,
//                LocalDBItems.fragmentName,
//                SelectCityFragment::class.qualifiedName
//            )
//
//            if (selectedCountry != null)
//                exchangeInfoService.put(
//                    SelectCityViewModel::class.qualifiedName!!,
//                    selectedCountry!!.country!!
//                )
        }


    }

    private fun unselectPreviousCountry() {
        for (country in countriesList) {
            if (country.isSelected!!) {
                country.isSelected = false
                countryAdapter?.notifyItemChanged(countryAdapter?.countriesList!!.indexOf(country))
                return
            }
        }
    }

    private fun initCountriesList() {
        Thread {
            val countries =
                Localisation.getAllCountriesStatesAndCities()
            Handler(Looper.getMainLooper()).post {
                for (country in countries) {
                    countriesList.add(CountryObj(country, false))
                }
                countryAdapter!!.notifyDataSetChanged()
            }
        }.start()
    }


    fun close() {
        navigationService.closeCurrentActivity()
    }

    fun searchByName(searchedString: String) {
        val auxSearchedCountries = java.util.ArrayList<CountryObj>()
        if (countriesList.isNotEmpty()) {
            for (item in countriesList) {
                val mySearchedString = searchedString.lowercase(Locale.ROOT)
                val itemString = item.country!!.name?.lowercase(Locale.ROOT)

                if (itemString!!.contains(mySearchedString) || mySearchedString.isEmpty()) {
                    auxSearchedCountries.add(item)
                }
            }
        }
        countryAdapter!!.filterList(auxSearchedCountries)
    }
}