package com.buzuriu.dogapp.viewModels

import android.util.Log
import com.buzuriu.dogapp.adapters.BreedAdapter
import com.buzuriu.dogapp.models.BreedObj
import com.buzuriu.dogapp.utils.BreedsFile
import java.lang.Exception

class SelectBreedViewModel : BaseViewModel(){

    private var breedsList = ArrayList<BreedObj>()
    var breedAdapter: BreedAdapter? = null
    private var selectedBreed: BreedObj? = null

    init {
        initBreedsList();

    }

    private fun initBreedsList() {
       /* BreedsFile.breedsList.forEach {
            Log.d("MyTAG",it)
        }*/
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

        /*unselectPreviousCountry()*/
        breedObj.isSelected = true
        selectedBreed = breedObj
        /*blockActionIfCountryNotSelected.value = false*/
        /*breedAdapter?.notifyItemChanged(breedAdapter?.breedsList!!.indexOf(breedObj))*/
    }

}