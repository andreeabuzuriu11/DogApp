package com.buzuriu.dogapp.viewModels

import com.buzuriu.dogapp.adapters.FilterAdapter
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.models.IFilterObj
import com.buzuriu.dogapp.utils.FilterItems
import com.buzuriu.dogapp.views.main.ui.map.MapViewModel

class FilterMeetingsViewModel : BaseViewModel(){

    var filterByTimeList =  FilterItems.filterByTimeItems
    var filterAdapter: FilterAdapter? = null
    private var selectedFilter: IFilterObj? = null

    init {
        filterAdapter = FilterAdapter(filterByTimeList, this)
        checkOtherFilterSelectedBefore()
    }

    fun checkOtherFilterSelectedBefore()
    {
        val isRefreshNeeded = dataExchangeService.get<Boolean>(this::class.qualifiedName!!)
        if (isRefreshNeeded!=null && isRefreshNeeded)
        {
            unselectPreviousFilters()
        }
    }

    fun saveFilter()
    {
        if (selectedFilter == null)
        {
            dialogService.showSnackbar("Please select a filter")
            return
        }

        dataExchangeService.put(MapViewModel::class.java.name, selectedFilter!!)
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