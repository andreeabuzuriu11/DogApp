package com.buzuriu.dogapp.viewModels

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.listeners.IClickListener
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.utils.ImageUtils
import com.buzuriu.dogapp.views.AddDogActivity
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

class DogDetailViewModel : BaseViewModel() {

    var dog = MutableLiveData<DogObj>()
/*    var dogPlaceHolder : MutableLiveData<Drawable>*/

    init {
        /*dogPlaceHolder = MutableLiveData<Drawable>(getDogPlaceHolder())*/
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
            }
        })
    }

    fun deleteDogFromDatabase()
    {


    }

/*    @SuppressLint("UseCompatLoadingForDrawables")
    private fun getDogPlaceHolder(): Drawable? {
        return activityService.activity!!.getDrawable(ImageUtils.getDogPlaceholder())
    }*/

}