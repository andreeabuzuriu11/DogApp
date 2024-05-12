package com.buzuriu.dogapp.models

class FilterByDogTemperamentObj : IFilterObj {
    var temperament = ""

    constructor(name: String, isSelected: Boolean) :
            super(name, isSelected) {
        this.temperament = temperament
    }
}