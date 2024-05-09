package com.buzuriu.dogapp.models

import com.beastwall.localisation.model.City

class CityObj {
    var city: City? = null
    var isSelected: Boolean? = false

    constructor(city: City?, isSelected: Boolean) {
        this.city = city
        this.isSelected = isSelected
    }
}