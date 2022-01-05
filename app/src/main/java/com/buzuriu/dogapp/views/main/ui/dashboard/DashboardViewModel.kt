package com.buzuriu.dogapp.views.main.ui.dashboard

import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.adapters.DogAdapter
import com.buzuriu.dogapp.enums.AgeEnum
import com.buzuriu.dogapp.enums.GenderEnum
import com.buzuriu.dogapp.listeners.IGetUserDogListListener
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.viewModels.BaseViewModel
import com.buzuriu.dogapp.views.AddDogActivity
import kotlinx.coroutines.launch

class DashboardViewModel : BaseViewModel() {

    var dogsList : ArrayList<DogObj> = ArrayList()
    var dogAdapter : DogAdapter?

    init {
        dogAdapter = DogAdapter(dogsList, ::selectedDog)

        dogAdapter!!.notifyDataSetChanged()

        viewModelScope.launch {
            databaseService.fetchUserDogs(currentUser!!.uid, object: IGetUserDogListListener {
                override fun getDogList(dogList: ArrayList<DogObj>) {
                    if(dogList.isNotEmpty())
                    {
                        dogsList.addAll(dogList)
                        dogAdapter!!.notifyDataSetChanged()
                    }
                }
            })
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