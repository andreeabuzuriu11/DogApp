package com.buzuriu.dogapp.viewModels

import com.buzuriu.dogapp.adapters.FilterAdapter
import com.buzuriu.dogapp.models.IFilterObj
import com.buzuriu.dogapp.utils.FilterItems
import com.buzuriu.dogapp.views.main.ui.map.MapViewModel

class FilterMeetingsViewModel : BaseViewModel(){

    var filterByTimeList =  FilterItems.filterByTimeItems
    var filterByDogGenderList = FilterItems.filterByDogGenderItems
    var filterAdapterTime: FilterAdapter? = null
    var filterAdapterDogGender: FilterAdapter? = null

    init {
        filterAdapterTime = FilterAdapter(filterByTimeList, this)
        filterAdapterDogGender = FilterAdapter(filterByDogGenderList, this)
        refreshFilters()

    }

    private fun refreshFilters()
    {
        for (i in filterByTimeList)
            i.isSelected = false
        for (i in filterByDogGenderList)
            i.isSelected = false
    }

    fun saveFilter()
    {
        var listOfCheckedFilters = ArrayList<IFilterObj>()

        for (i in filterByTimeList)
            if (i.isSelected == true)
                listOfCheckedFilters.add(i)
        for (i in filterByDogGenderList)
            if (i.isSelected == true)
                listOfCheckedFilters.add(i)

        dataExchangeService.put(MapViewModel::class.java.name, listOfCheckedFilters)

        navigationService.closeCurrentActivity()
    }
    fun checkSelectedFilter(position: Int, filterList: List<IFilterObj>, adapter: FilterAdapter)
    {
        for (pos in filterList.indices)
        {
            if (pos == position && !filterList[pos].isSelected!!)
            {
                filterList[pos].isSelected = true
            }
            else if (pos != position && filterList[pos].isSelected!!)
            {
                filterList[pos].isSelected = false
            }
            adapter.notifyItemChanged(pos)
        }

    }

    fun close()
    {
        navigationService.closeCurrentActivity()
    }
}