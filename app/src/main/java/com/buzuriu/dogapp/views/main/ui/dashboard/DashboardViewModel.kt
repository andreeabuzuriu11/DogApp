package com.buzuriu.dogapp.views.main.ui.dashboard

import com.buzuriu.dogapp.adapters.DogAdapter
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.viewModels.BaseViewModel
import com.buzuriu.dogapp.views.AddDogActivity


class DashboardViewModel : BaseViewModel() {

    var dogsList : ArrayList<DogObj> = ArrayList()
    var dogAdapter : DogAdapter?

    init {
        var dogsFromLocalDB = localDatabaseService.get<ArrayList<DogObj>>("localDogsList")
        if (dogsFromLocalDB != null) {
            dogsList.addAll(dogsFromLocalDB)
        }
        dogAdapter = DogAdapter(dogsList, ::selectedDog)
        dogAdapter!!.notifyDataSetChanged()

    }
    private fun selectedDog(dogObj: DogObj)
    {

    }

    fun addDog()
    {
        navigationService.navigateToActivity(AddDogActivity::class.java, false);
    }


}