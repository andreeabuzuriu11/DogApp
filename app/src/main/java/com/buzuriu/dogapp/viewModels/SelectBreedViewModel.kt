package com.buzuriu.dogapp.viewModels

import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.adapters.BreedAdapter
import com.buzuriu.dogapp.models.BreedObj
import com.buzuriu.dogapp.utils.BreedsFile

class SelectBreedViewModel : BaseViewModel(){

    private var breedsList = ArrayList<BreedObj>()
    var breedAdapter: BreedAdapter? = null
    private var selectedBreed: BreedObj? = null

    init {
        initBreedsList()
    }

    private fun initBreedsList() {
        for(breedName in BreedsFile.breedsList)
            breedsList.add(BreedObj(breedName))
        breedAdapter = BreedAdapter(breedsList, this)
    }

    fun close() {
        navigationService.closeCurrentActivity()
    }

    fun saveBreed() {
        if(selectedBreed == null)
        {
            dialogService.showSnackbar("Please select a breed")
            return
        }
        dataExchangeService.put(AddDogViewModel::class.qualifiedName!!, selectedBreed!!)
        close()
    }

    fun selectBreed(breedObj: BreedObj) {

        unselectPreviousCountry()
        breedObj.isSelected = true
        selectedBreed = breedObj

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