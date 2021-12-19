package com.buzuriu.dogapp.models

import com.buzuriu.dogapp.enums.AgeEnum
import com.buzuriu.dogapp.enums.GenderEnum

class DogObj {
    var name: String = ""
    var ageValue: Int = -1
    var ageString: AgeEnum? = null
    var breed: String? = null
    var gender: GenderEnum? = null
    var imageURL: String = ""

    constructor(name: String, ageValue: Int, ageString: AgeEnum, breed:String, gender: GenderEnum, imageURL: String)
    {
        this.name = name
        this.ageValue = ageValue
        this.ageString = ageString
        this.breed = breed
        this.gender = gender
        this.imageURL = imageURL
    }

}