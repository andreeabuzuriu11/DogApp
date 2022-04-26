package com.buzuriu.dogapp.models

abstract class IFilterObj {
    var name: String? = null
    var isSelected: Boolean? = null

    constructor(name: String, isSelected: Boolean) {
        this.name = name
        this.isSelected = isSelected
    }
}