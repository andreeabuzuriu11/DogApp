package com.buzuriu.dogapp.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.models.DogObj

class DogDetailViewModel : BaseViewModel() {

    var dog = DogObj()

    init {
        dog = dataExchangeService.get<DogObj>(this::class.java.name)!!
        Log.d("andreea ageString= ", dog.toString())




    }

}