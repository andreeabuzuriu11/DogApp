package com.buzuriu.dogapp.viewModels

import com.beastwall.localisation.model.State
import com.buzuriu.dogapp.adapters.StateAdapter
import com.buzuriu.dogapp.models.StateObj

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

    }

    fun close() {
        navigationService.closeCurrentActivity()
    }
}