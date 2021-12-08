package com.buzuriu.dogapp.views.main.ui.dashboard

import com.buzuriu.dogapp.adapters.DogAdapter
import com.buzuriu.dogapp.enum.AgeEnum
import com.buzuriu.dogapp.enum.GenderEnum
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.viewModels.BaseViewModel

class DashboardViewModel : BaseViewModel() {

    var dogList : ArrayList<DogObj> = ArrayList()
    var dogAdapter : DogAdapter?

    init {
        dogList.add(DogObj("Rex",4, AgeEnum.YEARS, "Husky", GenderEnum.MALE, "dog_image.jpg"))
        dogList.add(DogObj("Max",5, AgeEnum.MONTHS, "Bichon", GenderEnum.MALE, "dog_image.jpg"))
        dogList.add(DogObj("Lorelei",3, AgeEnum.YEARS, "Husky", GenderEnum.FEMALE, "dog_image.jpg"))
        dogList.add(DogObj("Rex",4, AgeEnum.YEARS, "Husky", GenderEnum.MALE, "dog_image.jpg"))
        dogAdapter = DogAdapter(dogList, ::selectedDog)
    }
    private fun selectedDog(dogObj: DogObj)
    {

    }


}