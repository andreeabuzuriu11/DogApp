package com.buzuriu.dogapp.models

class CountryObj {
    var countryName: String? = null
    var isSelected: Boolean? = false


    constructor(countryName: String?, isSelected : Boolean) {
        this.countryName = countryName
        this.isSelected = isSelected
    }
}