package com.buzuriu.dogapp.viewModels

import com.buzuriu.dogapp.adapters.CountryAdapter
import com.buzuriu.dogapp.models.CountryObj
import com.buzuriu.dogapp.models.LocationObj
import com.buzuriu.dogapp.utils.LocalDBItems
import com.buzuriu.dogapp.viewModels.auth.RegisterViewModel
import com.buzuriu.dogapp.views.SelectStateFragment
import com.buzuriu.dogapp.views.main.ui.OverlayActivity
import java.util.*
import kotlin.collections.ArrayList


class SelectCountryViewModel : BaseViewModel() {

    var selectedCountry: CountryObj? = null
    var locationObj: LocationObj? = null
    var countryAdapter: CountryAdapter? = null
    private var countriesList: ArrayList<CountryObj> = ArrayList()

    init {
        countriesList =
            localDatabaseService.get<ArrayList<CountryObj>>(LocalDBItems.countries)!!
        countryAdapter = CountryAdapter(countriesList, this)
    }

    fun saveCountry() {

    }

    fun selectCountry(countryObj: CountryObj) {
        unselectPreviousCountry()
        countryObj.isSelected = true
        selectedCountry = countryObj
        locationObj = LocationObj(countryObj.country!!, null, null)

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
                    locationObj!!
                )

        } else {
            exchangeInfoService.put(RegisterViewModel::class.qualifiedName!!, locationObj!!)
            close()
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