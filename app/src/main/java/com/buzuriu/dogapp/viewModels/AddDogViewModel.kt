package com.buzuriu.dogapp.viewModels

import android.util.Log
import android.widget.RadioGroup
import androidx.compose.ui.input.key.Key.Companion.D
import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.enums.AgeEnum
import com.buzuriu.dogapp.enums.GenderEnum
import com.buzuriu.dogapp.models.BreedObj
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.utils.StringUtils
import com.buzuriu.dogapp.views.SelectBreedFragment
import com.buzuriu.dogapp.views.main.ui.OverlayActivity

class AddDogViewModel : BaseViewModel() {

    var spinnerEntries = listOf(AgeEnum.MONTHS.toString(), AgeEnum.YEARS.toString())

    var name = MutableLiveData("")
    var breed = MutableLiveData("")
    var ageValue = MutableLiveData("")
    var ageString = MutableLiveData("")
    var imageURL = MutableLiveData<String>()
    var currentGender: GenderEnum? = null
    var currentGenderString:String? = null




    init{

    }

    override fun onResume()
    {
        val selectedBreed = dataExchangeService.get<BreedObj>(this::class.qualifiedName!!)
        if (selectedBreed!=null)
        {
            breed.value = selectedBreed.breedName
        }

    }

    fun setGenderType(gender : GenderEnum)
    {
        if(gender == GenderEnum.MALE)
            currentGenderString = "male"
        else
            currentGenderString = "female"
    }

    fun addDog()
    {
        if(!areFieldsCompleted()) return

        Log.d("info name=", name.value.toString())
        Log.d("info ageValue=", ageValue.value.toString())
        Log.d("info ageString=", ageString.value.toString())
        Log.d("info breed=", breed.value.toString())
        Log.d("info GenderString=", currentGenderString.toString())

        //TODO add dog image

        val uid = StringUtils.getRandomUID()
        val dog = DogObj(
            uid,
            name.value!!,
            ageValue.value!!,
            ageString.value!!,
            breed.value!!,
            currentGenderString!!)
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

    private fun areFieldsCompleted() :Boolean {
        if(name.value.isNullOrEmpty()) {
            dialogService.showSnackbar("Please add a name")
            return false
        }
        if(breed.value.isNullOrEmpty()) {
            dialogService.showSnackbar("Please add a breed")
            return false
        }
        if(ageValue.value.isNullOrEmpty()) {
            dialogService.showSnackbar("Please add an age")
            return false
        }
        if(currentGenderString.isNullOrEmpty()) {
            dialogService.showSnackbar("Please select a gender")
            return false
        }

        return true
    }

}