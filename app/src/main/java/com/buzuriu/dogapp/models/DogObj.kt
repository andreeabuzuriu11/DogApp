package com.buzuriu.dogapp.models

class DogObj {
    var uid: String = ""
    var name: String = ""
    var ageValue: String = ""
    var ageString: String = ""
    var breed: String = ""
    var gender: String = ""
    var imageUrl : String = ""
    var isSelected: Boolean? = false

    constructor()

    constructor(uid: String, name: String, ageValue: String, ageString: String, breed:String, gender: String, isSelected: Boolean?=null)
    {
        this.uid = uid
        this.name = name
        this.ageValue = ageValue
        this.ageString = ageString
        this.breed = breed
        this.gender = gender
        if(isSelected!=null)
        {
            this.isSelected = isSelected
        }
    }

}