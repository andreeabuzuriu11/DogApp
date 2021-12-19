package com.buzuriu.dogapp.viewModels

import java.lang.Exception

class SelectBreedViewModel : BaseViewModel(){

    private var breedsList = ArrayList<String>()

    init {
        initBreedsList();
    }

    private fun initBreedsList() {
        breedsList.add("Husky");
        breedsList.add("German Shepard");
    }

    fun close() {
        try {
            navigationService.closeCurrentActivity()
        }
        catch (e: Exception)
        {
        }

    }

    private fun unselectPreviousCountry() {
  /*      for (breed in breedsList) {
            if (breed.isSelected!!) {
                breed.isSelected = false
                return
            }
        }*/
    }
}