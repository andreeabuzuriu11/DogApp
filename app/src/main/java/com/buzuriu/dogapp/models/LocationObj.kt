package com.buzuriu.dogapp.models

import com.beastwall.localisation.model.City
import com.beastwall.localisation.model.Country
import com.beastwall.localisation.model.State

class LocationObj {
    var country: Country? = null
    var state: State? = null
    var city: City? = null

    constructor(country: Country, state: State?, city: City?) {
        this.country = country
        this.state = state
        this.city = city
    }

    override fun toString(): String {
        var finalString = ""

        if (country!!.name.isNotEmpty())
            finalString += country!!.name

        if (state!!.name.isNotEmpty())
            finalString += ", ${state!!.name}"

        if (city!!.name.isNotEmpty())
            finalString += ", ${city!!.name}"

        return finalString
    }

}