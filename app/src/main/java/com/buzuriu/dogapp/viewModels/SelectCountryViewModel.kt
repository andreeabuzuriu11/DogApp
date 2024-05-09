package com.buzuriu.dogapp.viewModels

import android.os.Handler
import android.os.Looper
import com.beastwall.localisation.Localisation
import com.buzuriu.dogapp.adapters.CountryAdapter
import com.buzuriu.dogapp.models.CountryObj
import com.buzuriu.dogapp.utils.LocalDBItems
import com.buzuriu.dogapp.views.SelectCityFragment
import com.buzuriu.dogapp.views.main.ui.OverlayActivity


class SelectCountryViewModel : BaseViewModel() {

    var selectedCountry: CountryObj? = null
    var countryAdapter: CountryAdapter? = null
    private var countriesList: ArrayList<CountryObj> = ArrayList()

    init {
        countryAdapter = CountryAdapter(countriesList, this)
        initCountriesList()
    }

    fun saveCountry()
    {

    }

    fun selectCountry(countryObj: CountryObj) {

        countryObj.isSelected = true
        selectedCountry = countryObj

        countryAdapter?.notifyItemChanged(countryAdapter?.countriesList!!.indexOf(countryObj))

        navigationService.showOverlay(
            OverlayActivity::class.java,
            false,
            LocalDBItems.fragmentName,
            SelectCityFragment::class.qualifiedName
        )

        if (selectedCountry != null)
            exchangeInfoService.put(
                SelectCityViewModel::class.qualifiedName!!,
                selectedCountry!!.country!!
            )
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
}