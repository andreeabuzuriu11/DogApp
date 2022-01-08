package com.buzuriu.dogapp.viewModels

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.utils.ImageUtils

class DogDetailViewModel : BaseViewModel() {

    var dog = MutableLiveData<DogObj>()
    var dogPlaceHolder : MutableLiveData<Drawable>
   // var dogBitmap: MutableLiveData<Bitmap>


    init {
        dogPlaceHolder = MutableLiveData<Drawable>(getDogPlaceHolder())
     //   dogBitmap =

        dog.value = dataExchangeService.get<DogObj>(this::class.java.name)!!

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun getDogPlaceHolder(): Drawable? {
        return activityService.activity!!.getDrawable(ImageUtils.getDogPlaceholder())
    }

}