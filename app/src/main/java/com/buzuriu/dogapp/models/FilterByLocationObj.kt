package com.buzuriu.dogapp.models

class FilterByLocationObj : IFilterObj {
    var distance: Int? = null

    constructor(name: String, distance: Int, isSelected: Boolean) :
            super(name, isSelected) {
        this.distance = distance
    }
}