package com.buzuriu.dogapp.viewModels

import com.buzuriu.dogapp.adapters.DogNameAdapter
import com.buzuriu.dogapp.models.BreedObj
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.views.AddMeetingActivity
import java.util.*
import kotlin.collections.ArrayList

class SelectDogViewModel : BaseViewModel() {

    var dogNameAdapter: DogNameAdapter? = null
    private var dogsList = ArrayList<DogObj>()
    private var selectedDog: DogObj? = null

    init {
        var dogsList = localDatabaseService.get<ArrayList<DogObj>>("localDogsList")
        dogNameAdapter = DogNameAdapter(dogsList!!, this)

        var dogName = dataExchangeService.get<String>(this::class.qualifiedName!!)
        if (dogName!=null)
        {
            var dog = dogsList.find { x -> x.name == dogName}
            selectDog(dog!!)
        }
    }


    fun saveDog()
    {
        if(selectedDog == null)
        {
            dialogService.showSnackbar("Please select a dog")
            return
        }
        dataExchangeService.put(AddMeetingViewModel::class.java.name, selectedDog!!)
        close()
    }


    fun selectDog(dogObj: DogObj) {
        unselectPreviousDog()
        dogObj.isSelected = true
        selectedDog = dogObj

        dogNameAdapter!!.notifyItemChanged(dogNameAdapter!!.dogsList.indexOf(dogObj))
    }

    private fun unselectPreviousDog()
    {
        for (dog in dogsList) {
            if (dog.isSelected!!) {
                dog.isSelected = false
                dogNameAdapter?.notifyItemChanged(dogsList.indexOf(dog))
                return
            }
        }
    }

    fun close() {
        navigationService.closeCurrentActivity()
    }

    fun searchByName(searchedString : String) {
        val auxSearchedDogs = ArrayList<DogObj>()
        if (dogsList.isNotEmpty())
        {
            for (item in dogsList)
            {
                val mySearchedString = searchedString.toLowerCase(Locale.ROOT)
                val itemString = item.name?.toLowerCase(Locale.ROOT)

                if(itemString!!.contains(mySearchedString) || mySearchedString.isEmpty())
                {
                    auxSearchedDogs.add(item)
                }
            }
        }
        dogNameAdapter!!.filterList(auxSearchedDogs)
    }
}