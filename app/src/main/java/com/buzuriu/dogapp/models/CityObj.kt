package com.buzuriu.dogapp.models

class CityObj {
    var cityName: String? = null
    var isSelected: Boolean? = false


    constructor(cityName: String?, isSelected : Boolean) {
        this.cityName = cityName
        this.isSelected = isSelected
    }
}