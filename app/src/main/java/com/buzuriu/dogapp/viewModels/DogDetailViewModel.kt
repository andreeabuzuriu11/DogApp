package com.buzuriu.dogapp.viewModels

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.utils.ImageUtils
import com.buzuriu.dogapp.views.AddDogActivity

class DogDetailViewModel : BaseViewModel() {

    var dog = MutableLiveData<DogObj>()
    var dogPlaceHolder : MutableLiveData<Drawable>

    init {
        dogPlaceHolder = MutableLiveData<Drawable>(getDogPlaceHolder())
        dog.value = dataExchangeService.get<DogObj>(this::class.java.name)!!

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun getDogPlaceHolder(): Drawable? {
        return activityService.activity!!.getDrawable(ImageUtils.getDogPlaceholder())
    }

    fun editDog()
    {
        dataExchangeService.put(AddDogViewModel::class.java.name, dog.value!!)
        navigationService.navigateToActivity(AddDogActivity::class.java)
    }

    fun deleteDog()
    {

    }

}