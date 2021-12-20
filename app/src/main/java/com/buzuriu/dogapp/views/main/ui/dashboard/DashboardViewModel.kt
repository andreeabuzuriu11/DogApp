package com.buzuriu.dogapp.views.main.ui.dashboard

import com.buzuriu.dogapp.adapters.DogAdapter
import com.buzuriu.dogapp.enums.AgeEnum
import com.buzuriu.dogapp.enums.GenderEnum
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.viewModels.BaseViewModel
import com.buzuriu.dogapp.views.AddDogActivity

class DashboardViewModel : BaseViewModel() {

    var dogList : ArrayList<DogObj> = ArrayList()
    var dogAdapter : DogAdapter?

    init {
        dogList.add(DogObj("1","Rex","4", "MONTHS", "Husky", "male"))
        dogList.add(DogObj("2","Max","5", "YEARS", "Bichon", "male"))
        dogList.add(DogObj("3","Lorelei","3", "YEARS", "Husky", "female"))
        dogList.add(DogObj("4","Rex","4", "YEARS", "Husky","female"))
        dogList.add(DogObj("5","Rex","4", "YEARS", "Husky", "male"))
        dogList.add(DogObj("6","Rex","4", "YEARS", "Husky", "female"))
        dogList.add(DogObj("7","Max","5", "MONTHS", "Bichon", "female"))
        dogList.add(DogObj("8","Lorelei","3", "YEARS", "Husky", "male"))
        dogAdapter = DogAdapter(dogList, ::selectedDog)
    }
    private fun selectedDog(dogObj: DogObj)
    {

    }
    fun addDog()
    {
        navigationService.navigateToActivity(AddDogActivity::class.java, false);
    }


}