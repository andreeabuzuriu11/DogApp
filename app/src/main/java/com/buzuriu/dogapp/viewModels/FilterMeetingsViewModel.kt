package com.buzuriu.dogapp.viewModels

import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.adapters.FilterAdapter
import com.buzuriu.dogapp.models.DogPersonality
import com.buzuriu.dogapp.models.FilterByDogBreedObj
import com.buzuriu.dogapp.models.IFilterObj
import com.buzuriu.dogapp.utils.FilterItems
import com.buzuriu.dogapp.utils.LocalDBItems
import com.buzuriu.dogapp.utils.StringUtils.Companion.removeFirstCharacterIfWhitespace
import com.buzuriu.dogapp.views.main.ui.map.MapViewModel

class FilterMeetingsViewModel : BaseViewModel() {

    var filterAdapterTime: FilterAdapter? = null
    var filterAdapterDogGender: FilterAdapter? = null
    var filterAdapterUserGender: FilterAdapter? = null
    var filterAdapterDogBreed: FilterAdapter? = null
    var breed = MutableLiveData<String>()

    private var filterByTimeList = FilterItems.filterByTimeItems
    private var filterByDogGenderList = FilterItems.filterByDogGenderItems
    private var filterByUserGenderList = FilterItems.filterByUserGenderItems
    private var filterByDogBreedListString: ArrayList<String>?
    private var filterByDogBreedList = ArrayList<IFilterObj>()
    private var filterByDogTemperamentList = ArrayList<IFilterObj>()
    internal var selectedTemperamentObj = String()
    internal var selectedEnergyLevel = String()


    init {
        filterAdapterTime = FilterAdapter(filterByTimeList, this)
        filterAdapterDogGender = FilterAdapter(filterByDogGenderList, this)
        filterAdapterUserGender = FilterAdapter(filterByUserGenderList, this)
        filterByDogBreedListString =
            localDatabaseService.get<ArrayList<String>>(this::class.qualifiedName!!)
        if (filterByDogBreedListString != null) {
            for (item in filterByDogBreedListString!!) {
                filterByDogBreedList.add(FilterByDogBreedObj(item, false))
            }
        }
        filterAdapterDogBreed = FilterAdapter(filterByDogBreedList, this)


    }


    private fun refreshFilters() {
        for (i in filterByTimeList)
            i.isSelected = false
        for (i in filterByDogGenderList)
            i.isSelected = false
        for (i in filterByUserGenderList)
            i.isSelected = false
        for (i in filterByDogBreedList)
            i.isSelected = false
    }

    fun saveFilter() {
        val listOfCheckedFilters = ArrayList<IFilterObj>()

        for (i in filterByTimeList)
            if (i.isSelected == true)
                listOfCheckedFilters.add(i)
        for (i in filterByDogGenderList)
            if (i.isSelected == true)
                listOfCheckedFilters.add(i)
        for (i in filterByUserGenderList)
            if (i.isSelected == true)
                listOfCheckedFilters.add(i)
        for (i in filterByDogBreedList)
            if (i.isSelected == true)
                listOfCheckedFilters.add(i)

        exchangeInfoService.put(MapViewModel::class.java.name, listOfCheckedFilters)

        navigationService.closeCurrentActivity()
    }

    fun checkSelectedFilter(position: Int, filterList: List<IFilterObj>, adapter: FilterAdapter) {
        for (pos in filterList.indices) {
            if (pos == position && !filterList[pos].isSelected!!) {
                filterList[pos].isSelected = true
                adapter.notifyItemChanged(pos)
            } else if (pos != position && filterList[pos].isSelected!!) {
                filterList[pos].isSelected = false
                adapter.notifyItemChanged(pos)
            }
        }
    }

    fun close() {
        refreshFilters()
        navigationService.closeCurrentActivity()
    }

    override fun onDestroy() {
        refreshFilters()
    }

    fun getTemperamentList(): List<String> {
        val list = ArrayList<String>()

        list.add("All")
        // extract all temperaments ever, only once
        val dogPersonalityList =
            localDatabaseService.get<List<DogPersonality>>(LocalDBItems.dogPersonalityList)!!
        for (dogPersonality in dogPersonalityList) {
            val temp = dogPersonality.temperament

            val splitArray = temp.split(",").toTypedArray()

            for (item in splitArray) {
                if (!(list.contains(item))) {
                    val itemNoWhitespace = removeFirstCharacterIfWhitespace(item)
                    if (itemNoWhitespace.isNotEmpty())
                        list.add(itemNoWhitespace)
                }
            }
        }

        return list
    }

    fun getEnergyLevelList(): List<String> {
        val list = ArrayList<String>()
        list.add("All")

        // extract all temperaments ever, only once
        val dogPersonalityList =
            localDatabaseService.get<List<DogPersonality>>(LocalDBItems.dogPersonalityList)!!
        for (dogPersonality in dogPersonalityList) {
            val temp = dogPersonality.energy_level_category

            val splitArray = temp.split(",").toTypedArray()

            for (item in splitArray) {
                if (!(list.contains(item))) {
                    val itemNoWhitespace = removeFirstCharacterIfWhitespace(item)
                    if (itemNoWhitespace.isNotEmpty())
                        list.add(itemNoWhitespace)
                }
            }
        }

        return list
    }

}