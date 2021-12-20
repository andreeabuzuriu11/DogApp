package com.buzuriu.dogapp.models

import com.buzuriu.dogapp.enums.AgeEnum
import com.buzuriu.dogapp.enums.GenderEnum

class DogObj {
    var uid: String = ""
    var name: String = ""
    var ageValue: String = ""
    var ageString: AgeEnum? = null
    var breed: String = ""
    var gender: String = ""
    var imageURL: String = ""

    constructor(uid: String, name: String, ageValue: String, ageString: AgeEnum, breed:String, gender: String, imageURL: String)
    {
        this.uid = uid
        this.name = name
        this.ageValue = ageValue
        this.ageString = ageString
        this.breed = breed
        this.gender = gender
        this.imageURL = imageURL
    }

}