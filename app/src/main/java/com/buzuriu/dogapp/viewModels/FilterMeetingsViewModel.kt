package com.buzuriu.dogapp.viewModels

import com.buzuriu.dogapp.adapters.FilterAdapter
import com.buzuriu.dogapp.utils.FilterItems

class FilterMeetingsViewModel : BaseViewModel(){

    var filterByTimeList =  FilterItems.filterByTimeItems
    var filterAdapter: FilterAdapter? = null

    init {
        filterAdapter = FilterAdapter(filterByTimeList, this)
    }

    fun close()
    {
        navigationService.closeCurrentActivity()
    }

}