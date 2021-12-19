package com.buzuriu.dogapp.viewModels

import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.adapters.BreedAdapter
import com.buzuriu.dogapp.models.BreedObj
import com.buzuriu.dogapp.utils.BreedsFile
import java.lang.Exception

class SelectBreedViewModel : BaseViewModel(){

    private var breedsList = ArrayList<BreedObj>()
    public var isButtonEnabled =  MutableLiveData(false)
    var breedAdapter: BreedAdapter? = null
    private var selectedBreed: BreedObj? = null

    init {
        isButtonEnabled = MutableLiveData(false)
        initBreedsList();

    }

    private fun initBreedsList() {
        for(breedName in BreedsFile.breedsList)
            breedsList.add(BreedObj(breedName))
        breedAdapter = BreedAdapter(breedsList, this)
    }

    fun close() {
        try {
            navigationService.closeCurrentActivity()
        }
        catch (e: Exception)
        {
        }
    }

    fun selectBreed(breedObj: BreedObj) {

        unselectPreviousCountry()
        breedObj.isSelected = true
        isButtonEnabled = MutableLiveData(true)
        selectedBreed = breedObj

        /*blockActionIfCountryNotSelected.value = false*/
        breedAdapter?.notifyItemChanged(breedAdapter?.breedsList!!.indexOf(breedObj))
    }

    private fun unselectPreviousCountry() {
        for (breed in breedsList) {
            if (breed.isSelected!!) {
                breed.isSelected = false
                breedAdapter?.notifyItemChanged(breedsList.indexOf(breed))
                return
            }
        }
    }

}