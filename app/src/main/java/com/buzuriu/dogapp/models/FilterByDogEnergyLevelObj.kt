package com.buzuriu.dogapp.models

class FilterByDogEnergyLevelObj : IFilterObj {

    constructor(name: String, isSelected: Boolean) :
            super(name, isSelected) {
        this.name = name
    }
}