package com.buzuriu.dogapp.models

import com.beastwall.localisation.model.State

class StateObj {
    var state: State? = null
    var isSelected: Boolean? = false


    constructor(state: State?, isSelected : Boolean) {
        this.state = state
        this.isSelected = isSelected
    }
}