package com.buzuriu.dogapp.viewModels

import com.beastwall.localisation.model.State
import com.buzuriu.dogapp.adapters.StateAdapter
import com.buzuriu.dogapp.models.StateObj
import com.buzuriu.dogapp.utils.LocalDBItems
import com.buzuriu.dogapp.views.SelectCityFragment
import com.buzuriu.dogapp.views.SelectStateFragment
import com.buzuriu.dogapp.views.main.ui.OverlayActivity

class SelectStateViewModel : BaseViewModel() {

    var selectedState: StateObj? = null
    var stateAdapter: StateAdapter? = null
    private var statesList: ArrayList<StateObj> = ArrayList()


    init {
        stateAdapter = StateAdapter(statesList, this)
        var states =
            exchangeInfoService.get<List<State>>(this::class.java.name)!!
        for (state in states) {
            statesList.add(StateObj(state, false))
        }
        stateAdapter!!.notifyDataSetChanged()
    }

    fun saveState() {

    }

    fun selectState(stateObj: StateObj) {
        stateObj.isSelected = true
        selectedState = stateObj

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
                stateObj.state!!.cities
            )


        }
    }

    fun close() {
        navigationService.closeCurrentActivity()
    }
}