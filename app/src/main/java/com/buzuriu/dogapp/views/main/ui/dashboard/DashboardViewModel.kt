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
/*        dogList.add(DogObj("1","Rex","4", "MONTHS", "Husky", "male"))
        dogList.add(DogObj("2","Max","5", "YEARS", "Bichon", "male"))
        dogList.add(DogObj("3","Lorelei","3", "YEARS", "Husky", "female"))
        dogList.add(DogObj("4","Rex","4", "YEARS", "Husky","female"))
        dogList.add(DogObj("5","Rex","4", "YEARS", "Husky", "male"))
        dogList.add(DogObj("6","Rex","4", "YEARS", "Husky", "female"))
        dogList.add(DogObj("7","Max","5", "MONTHS", "Bichon", "female"))
        dogList.add(DogObj("8","Lorelei","3", "YEARS", "Husky", "male"))*/

        dogAdapter = DogAdapter(dogsList, ::selectedDog)

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