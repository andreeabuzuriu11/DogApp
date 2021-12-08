package com.buzuriu.dogapp.models

class BreedObj {
    var breedName: String? = null
    var isSelected: Boolean = false

    constructor(breedName: String?, isSelected: Boolean?=null) {
        this.breedName = breedName
        if(isSelected!=null)
        {
            this.isSelected = isSelected
        }
    }
}