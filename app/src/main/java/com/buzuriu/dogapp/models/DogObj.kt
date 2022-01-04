package com.buzuriu.dogapp.models

import com.buzuriu.dogapp.enums.AgeEnum
import com.buzuriu.dogapp.enums.GenderEnum

class DogObj {
    var uid: String = ""
    var name: String = ""
    var ageValue: String = ""
    var ageString: String = ""
    var breed: String = ""
    var gender: String = ""
    var imageUrl : String = ""

    constructor()

    // TODO add imageURL
    constructor(uid: String, name: String, ageValue: String, ageString: String, breed:String, gender: String)
    {
        this.uid = uid
        this.name = name
        this.ageValue = ageValue
        this.ageString = ageString
        this.breed = breed
        this.gender = gender
    }

}