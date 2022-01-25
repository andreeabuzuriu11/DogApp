package com.buzuriu.dogapp.viewModels

import com.buzuriu.dogapp.adapters.DogNameAdapter
import com.buzuriu.dogapp.models.BreedObj
import com.buzuriu.dogapp.models.DogObj

class SelectDogViewModel : BaseViewModel() {

    var dogNameAdapter: DogNameAdapter? = null

    init {
        var listOfDogs = localDatabaseService.get<ArrayList<DogObj>>("localDogsList")
        dogNameAdapter = DogNameAdapter(listOfDogs!!, this)
        dogNameAdapter!!.notifyDataSetChanged()
    }

    fun saveDog()
    {}


    fun selectDog(dogObj: DogObj) {
        dogObj.isSelected = true
    }

    fun close() {
        navigationService.closeCurrentActivity()
    }
}