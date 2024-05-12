package com.buzuriu.dogapp.models

class FilterByDogTemperamentObj : IFilterObj {

    constructor(name: String, isSelected: Boolean) :
            super(name, isSelected) {
        this.name = name
    }
}