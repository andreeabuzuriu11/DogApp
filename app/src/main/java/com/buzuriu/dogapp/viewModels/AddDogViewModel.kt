package com.buzuriu.dogapp.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.enums.AgeEnum
import com.buzuriu.dogapp.models.BreedObj
import com.buzuriu.dogapp.views.SelectBreedFragment
import com.buzuriu.dogapp.views.main.ui.OverlayActivity

class AddDogViewModel : BaseViewModel() {

    var spinnerEntries = listOf(AgeEnum.MONTHS, AgeEnum.YEARS)

    var name = MutableLiveData<String>()
    var breed = MutableLiveData<String>()
    var ageValue = MutableLiveData<String>()
    var ageString = MutableLiveData(AgeEnum.MONTHS)
    var imageURL = MutableLiveData<String>()
    var radio_checked = MutableLiveData<Int>()

    init{
        radio_checked.postValue(R.id.maleRadioId) //def value
    }

    override fun onResume()
    {
        val selectedBreed = dataExchangeService.get<BreedObj>(this::class.qualifiedName!!)
        if (selectedBreed!=null)
        {
            breed.value = selectedBreed.breedName
        }
    }


    fun addDogInList()
    {

    }
    fun selectBreed()
    {

        navigationService.showOverlay(
            OverlayActivity::class.java,
            false,
            OverlayActivity.fragmentClassNameParam,
            SelectBreedFragment::class.qualifiedName
        )
    }
}