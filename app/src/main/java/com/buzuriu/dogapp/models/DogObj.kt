package com.buzuriu.dogapp.models

import com.buzuriu.dogapp.enum.AgeEnum
import com.buzuriu.dogapp.enum.GenderEnum

class DogObj {
    var name: String = ""
    var ageValue: Int = -1
    var ageString: AgeEnum? = null
    var breed: String? = null
    var gender: GenderEnum? = null
    var imageURL: String = ""
}