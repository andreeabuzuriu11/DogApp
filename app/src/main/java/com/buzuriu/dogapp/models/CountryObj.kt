package com.buzuriu.dogapp.models

import com.beastwall.localisation.model.Country

class CountryObj {
    var country: Country? = null
    var isSelected: Boolean? = false


    constructor(country: Country?, isSelected : Boolean) {
        this.country = country
        this.isSelected = isSelected
    }
}