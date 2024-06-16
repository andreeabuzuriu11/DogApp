package com.buzuriu.dogapp.viewModels

import com.buzuriu.dogapp.adapters.EditStateAdapter
import com.buzuriu.dogapp.models.LocationObj
import com.buzuriu.dogapp.models.StateObj
import com.buzuriu.dogapp.utils.LocalDBItems
import com.buzuriu.dogapp.views.EditCityFragment
import com.buzuriu.dogapp.views.SelectCityFragment
import com.buzuriu.dogapp.views.main.ui.OverlayActivity
import java.util.*

class EditStateViewModel : BaseViewModel() {

    var currentLocationObj: LocationObj? = null
    var selectedState: StateObj? = null
    var stateAdapter: EditStateAdapter? = null
    var locationObj: LocationObj? = null

    private var statesList: ArrayList<StateObj> = ArrayList()


    init {
        stateAdapter = EditStateAdapter(statesList, this)
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
                EditCityFragment::class.qualifiedName
            )

            exchangeInfoService.put(
                EditCityViewModel::class.qualifiedName!!,
                locationObj!!
            )
            close()

        } else {
            // just close and save as it is
            exchangeInfoService.put(EditAccountViewModel::class.qualifiedName!!, locationObj!!)
            close()
        }
    }

    fun close() {
        navigationService.closeFragment()
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