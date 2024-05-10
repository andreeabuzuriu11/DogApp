package com.buzuriu.dogapp.viewModels

import com.beastwall.localisation.model.State
import com.buzuriu.dogapp.adapters.StateAdapter
import com.buzuriu.dogapp.models.LocationObj
import com.buzuriu.dogapp.models.StateObj
import com.buzuriu.dogapp.utils.LocalDBItems
import com.buzuriu.dogapp.views.SelectCityFragment
import com.buzuriu.dogapp.views.main.ui.OverlayActivity
import java.util.*

class SelectStateViewModel : BaseViewModel() {

    var currentLocationObj : LocationObj? = null
    var selectedState: StateObj? = null
    var stateAdapter: StateAdapter? = null
    var locationObj: LocationObj? = null
    private var statesList: ArrayList<StateObj> = ArrayList()


    init {
        stateAdapter = StateAdapter(statesList, this)
         locationObj =
            exchangeInfoService.get<LocationObj>(this::class.java.name)!!
        var states = locationObj!!.country!!.states
        for (state in states) {
            statesList.add(StateObj(state, false))
        }
        stateAdapter!!.notifyDataSetChanged()
    }

    fun saveState() {

    }

    fun selectState(stateObj: StateObj) {
        unselectPreviousState()
        stateObj.isSelected = true
        selectedState = stateObj
        locationObj!!.state = stateObj.state

        stateAdapter?.notifyItemChanged(stateAdapter?.statesList!!.indexOf(stateObj))

        if (stateObj.state!!.cities.size != 0) {
            // has more states so navigate to states page
            navigationService.showOverlay(
                OverlayActivity::class.java,
                false,
                LocalDBItems.fragmentName,
                SelectCityFragment::class.qualifiedName
            )

            exchangeInfoService.put(
                SelectCityViewModel::class.qualifiedName!!,
                locationObj!!
            )


        }
    }

    fun close() {
        navigationService.closeCurrentActivity()
    }

    private fun unselectPreviousState() {
        for (state in statesList) {
            if (state.isSelected!!) {
                state.isSelected = false
                stateAdapter?.notifyItemChanged(stateAdapter?.statesList!!.indexOf(state))
                return
            }
        }
    }

    fun searchByName(searchedString: String) {
        val auxSearchedStateList = java.util.ArrayList<StateObj>()
        if (statesList.isNotEmpty()) {
            for (item in statesList) {
                val mySearchedString = searchedString.lowercase(Locale.ROOT)
                val itemString = item.state!!.name?.lowercase(Locale.ROOT)

                if (itemString!!.contains(mySearchedString) || mySearchedString.isEmpty()) {
                    auxSearchedStateList.add(item)
                }
            }
        }
        stateAdapter!!.filterList(auxSearchedStateList)
    }
}