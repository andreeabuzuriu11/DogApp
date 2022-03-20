package com.buzuriu.dogapp.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.listeners.IClickListener
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.views.AddDogActivity
import com.buzuriu.dogapp.views.main.ui.my_dogs.MyDogsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class DogDetailViewModel : BaseViewModel() {

    var dog = MutableLiveData<DogObj>()

    init {
        dog.value = dataExchangeService.get<DogObj>(this::class.java.name)!!
    }

    override fun onResume()
    {
        super.onResume()
        val editedDog = dataExchangeService.get<DogObj>(this::class.java.name)
        if(editedDog != null)
        {
            dog.value = editedDog!!
        }
    }

    fun editDog()
    {
        dataExchangeService.put(AddDogViewModel::class.java.name, dog.value!!)
        navigationService.navigateToActivity(AddDogActivity::class.java)
    }

    fun deleteDog()
    {
        dialogService.showAlertDialog("Delete dog?", "Are you sure you want to delete ${dog.value!!.name}? This action cannot be undone.", "Yes, delete it", object :
            IClickListener {
            override fun clicked() {
                deleteDogFromDatabase()
                dataExchangeService.put(MyDogsViewModel::class.java.name, true) // is refresh list needed
            }
        })
    }

    fun deleteDogFromDatabase()
    {
        viewModelScope.launch(Dispatchers.IO) {
            databaseService.deleteDog(currentUser!!.uid, dog.value!!.uid, object :
            IOnCompleteListener {
                override fun onComplete(successful: Boolean, exception: Exception?) {
                    val allDogsList = localDatabaseService.get<ArrayList<DogObj>>("localDogsList")
                    allDogsList!!.remove(dog.value!!)

                    localDatabaseService.add("localDogsList", allDogsList)
                    navigationService.closeCurrentActivity()
                }
            })
        }
    }

}