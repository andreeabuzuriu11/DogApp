package com.buzuriu.dogapp.views.main.ui.dashboard

import android.util.Log
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

    override fun onResume() {
        super.onResume()
        var isRefreshListNeeded : Boolean? = dataExchangeService.get<Boolean>(this::class.qualifiedName!!)
        var dogsFromLocalDB = localDatabaseService.get<ArrayList<DogObj>>("localDogsList")
        if(isRefreshListNeeded!=null && isRefreshListNeeded)
        {
            dogsList.clear()

            if (dogsFromLocalDB != null) {
                Log.d("andreed", "List was refreshed")
                dogsList.addAll(dogsFromLocalDB)
                dogAdapter!!.notifyDataSetChanged()
            }
        }

    }

    private fun selectedDog(dogObj: DogObj)
    {

    }

    fun addDog()
    {
        navigationService.navigateToActivity(AddDogActivity::class.java, false);
    }


}