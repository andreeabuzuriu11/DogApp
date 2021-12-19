package com.buzuriu.dogapp.viewModels

import android.util.Log
import com.buzuriu.dogapp.utils.BreedsFile
import java.lang.Exception

class SelectBreedViewModel : BaseViewModel(){

    private var breedsList = ArrayList<String>()

    init {
        initBreedsList();

    }

    private fun initBreedsList() {
        BreedsFile.breedsList.forEach {
            Log.d("MyTAG",it)
        }
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