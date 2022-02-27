package com.buzuriu.dogapp.viewModels

import com.buzuriu.dogapp.adapters.FilterAdapter
import com.buzuriu.dogapp.models.FilterByTimeObj
import com.buzuriu.dogapp.models.IFilterObj
import com.buzuriu.dogapp.utils.FilterItems

class FilterMeetingsViewModel : BaseViewModel(){

    var filterByTimeList =  FilterItems.filterByTimeItems
    var filterAdapter: FilterAdapter? = null
    private var selectedBreed: IFilterObj? = null

    init {
        filterAdapter = FilterAdapter(filterByTimeList, this)
    }

    fun close()
    {
        navigationService.closeCurrentActivity()
    }

    fun selectTime(filterObj : IFilterObj)
    {
        unselectPreviousFilters()
        filterObj.isSelected = true
        selectedBreed = filterObj

        filterAdapter?.notifyItemChanged(filterAdapter?.filterList!!.indexOf(filterObj))
    }

    fun unselectPreviousFilters()
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