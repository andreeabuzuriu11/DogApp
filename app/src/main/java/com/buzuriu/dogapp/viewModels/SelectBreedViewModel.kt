package com.buzuriu.dogapp.viewModels

import com.buzuriu.dogapp.adapters.BreedAdapter
import com.buzuriu.dogapp.models.BreedObj
import com.buzuriu.dogapp.utils.BreedsFile
import java.util.*

class SelectBreedViewModel : BaseViewModel() {

    private var breedsList = ArrayList<BreedObj>()
    private var selectedBreed: BreedObj? = null
    var breedAdapter: BreedAdapter? = null

    init {
        initBreedsList()
        getSelectedBreed()
    }

    private fun initBreedsList() {
        for (breedName in BreedsFile.breedsList)
            breedsList.add(BreedObj(breedName))
        breedAdapter = BreedAdapter(breedsList, this)
    }

    private fun getSelectedBreed() {
        val breedName = exchangeInfoService.get<String>(this::class.qualifiedName!!)
        if (!breedName.isNullOrEmpty()) {
            val breed = breedsList.find { x -> x.breedName == breedName }
            selectBreed(breed!!)
        }
    }

    private fun unselectPreviousBreed() {
        for (breed in breedsList) {
            if (breed.isSelected!!) {
                breed.isSelected = false
                breedAdapter?.notifyItemChanged(breedsList.indexOf(breed))
                return
            }
        }
    }

    fun close() {
        navigationService.closeCurrentActivity()
    }

    fun saveBreed() {
        if (selectedBreed == null) {
            snackMessageService.displaySnackBar("Please select a breed")
            return
        }
        exchangeInfoService.put(AddDogViewModel::class.qualifiedName!!, selectedBreed!!)
        close()
    }

    fun selectBreed(breedObj: BreedObj) {
        unselectPreviousBreed()
        breedObj.isSelected = true
        selectedBreed = breedObj

        breedAdapter?.notifyItemChanged(breedAdapter?.breedsList!!.indexOf(breedObj))
    }

    fun searchByName(searchedString: String) {
        val auxSearchedBreeds = ArrayList<BreedObj>()
        if (breedsList.isNotEmpty()) {
            for (item in breedsList) {
                val mySearchedString = searchedString.lowercase(Locale.ROOT)
                val itemString = item.breedName?.lowercase(Locale.ROOT)

                if (itemString!!.contains(mySearchedString) || mySearchedString.isEmpty()) {
                    auxSearchedBreeds.add(item)
                }
            }
        }
        breedAdapter!!.filterList(auxSearchedBreeds)
    }
}