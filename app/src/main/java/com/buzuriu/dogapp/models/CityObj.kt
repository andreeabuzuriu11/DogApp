package com.buzuriu.dogapp.models

class CityObj {
    var cityName: String? = null
    var isSelected: Boolean? = false


    constructor(countryName: String?, isSelected : Boolean) {
        this.cityName = countryName
        this.isSelected = isSelected
    }
}