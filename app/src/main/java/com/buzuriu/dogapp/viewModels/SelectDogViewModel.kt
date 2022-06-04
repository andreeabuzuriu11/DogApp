package com.buzuriu.dogapp.viewModels

import com.buzuriu.dogapp.adapters.DogNameAdapter
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.utils.LocalDBItems
import java.util.*

class SelectDogViewModel : BaseViewModel() {

    var dogNameAdapter: DogNameAdapter? = null
    private var dogsList = ArrayList<DogObj>()
    private var selectedDog: DogObj? = null

    init {
        initDogs()

        val dogName = exchangeInfoService.get<String>(this::class.qualifiedName!!)
        if (dogName != null) {
            val dog = dogsList.find { x -> x.name == dogName }
            selectDog(dog!!)
        }
    }

    private fun initDogs() {
        dogsList = localDatabaseService.get<ArrayList<DogObj>>(LocalDBItems.localDogsList)!!
        dogNameAdapter = DogNameAdapter(dogsList, this)
    }

    fun saveDog() {
        if (selectedDog == null) {
            snackMessageService.displaySnackBar("Please select a dog")
            return
        }
        exchangeInfoService.put(AddMeetingViewModel::class.java.name, selectedDog!!)
        close()
    }


    fun selectDog(dogObj: DogObj) {
        unselectPreviousDog()
        dogObj.isSelected = true
        selectedDog = dogObj

        dogNameAdapter?.notifyItemChanged(dogNameAdapter?.dogsList!!.indexOf(dogObj))

    }

    private fun unselectPreviousDog() {
        for (dog in dogsList) {
            if (dog.isSelected!!) {
                dog.isSelected = false
                dogNameAdapter?.notifyItemChanged(dogNameAdapter?.dogsList!!.indexOf(dog))
                return
            }
        }
    }

    fun close() {
        unselectPreviousDog()
        navigationService.closeCurrentActivity()
    }

    fun searchByName(searchedString: String) {
        val auxSearchedDogs = ArrayList<DogObj>()
        if (dogsList.isNotEmpty()) {
            for (item in dogsList) {
                val mySearchedString = searchedString.lowercase(Locale.ROOT)
                val itemString = item.name.lowercase(Locale.ROOT)

                if (itemString.contains(mySearchedString) || mySearchedString.isEmpty()) {
                    auxSearchedDogs.add(item)
                }
            }
        }
        dogNameAdapter!!.filterList(auxSearchedDogs)
    }
}