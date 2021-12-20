package com.buzuriu.dogapp.viewModels

import com.buzuriu.dogapp.adapters.BreedAdapter
import com.buzuriu.dogapp.models.BreedObj
import com.buzuriu.dogapp.utils.BreedsFile
import java.util.*
import kotlin.collections.ArrayList

class SelectBreedViewModel : BaseViewModel(){

    companion object {
        const val breedKey = "breedKey"
        const val currentBreedKey = "currentBreedKey"
    }

    private var breedsList = ArrayList<BreedObj>()
    private var selectedBreed: BreedObj? = null
    var breedAdapter: BreedAdapter? = null

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

        saveBreedInSharedPreferences()
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

    fun searchByName(searchedString : String) {
        val auxSearchedBreeds = ArrayList<BreedObj>()
        if (breedsList.isNotEmpty())
        {
            for (item in breedsList)
            {
                val mySearchedString = searchedString.toLowerCase(Locale.ROOT)
                val itemString = item.breedName?.toLowerCase(Locale.ROOT)

                if(itemString!!.contains(mySearchedString) || mySearchedString.isEmpty())
                {
                    auxSearchedBreeds.add(item)
                }
            }
        }
        breedAdapter!!.filterList(auxSearchedBreeds)
    }

    private fun saveBreedInSharedPreferences()
    {
        sharedPreferenceService.writeInSharedPref(currentBreedKey,selectedBreed!!)
    }
}