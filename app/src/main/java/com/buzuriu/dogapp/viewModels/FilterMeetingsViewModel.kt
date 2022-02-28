package com.buzuriu.dogapp.viewModels

import com.buzuriu.dogapp.adapters.FilterAdapter
import com.buzuriu.dogapp.models.IFilterObj
import com.buzuriu.dogapp.utils.FilterItems

class FilterMeetingsViewModel : BaseViewModel(){

    var filterByTimeList =  FilterItems.filterByTimeItems
    var filterAdapter: FilterAdapter? = null
    private var selectedFilter: IFilterObj? = null

    init {
        filterAdapter = FilterAdapter(filterByTimeList, this)
    }

    fun saveFilter()
    {
        unselectPreviousFilters()
        if (selectedFilter == null)
        {
            dialogService.showSnackbar("Please select a filter")
            return
        }

        dataExchangeService.put("MapViewModel", selectedFilter!!)
        navigationService.closeCurrentActivity()
    }

    fun close()
    {
        navigationService.closeCurrentActivity()
    }

    fun selectTime(filterObj : IFilterObj)
    {
        unselectPreviousFilters()
        filterObj.isSelected = true
        selectedFilter = filterObj

        filterAdapter?.notifyItemChanged(filterAdapter?.filterList!!.indexOf(filterObj))
    }

    private fun unselectPreviousFilters()
    {
        for (filter in filterByTimeList)
            if (filter.isSelected!!)
            {
                filter.isSelected = false
                filterAdapter?.notifyItemChanged(filterAdapter?.filterList!!.indexOf(filter))
                return
            }
    }


}