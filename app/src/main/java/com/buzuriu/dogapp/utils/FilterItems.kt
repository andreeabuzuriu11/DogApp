package com.buzuriu.dogapp.utils

import com.buzuriu.dogapp.models.FilterByDogGenderObj
import com.buzuriu.dogapp.models.FilterByTimeObj
import com.buzuriu.dogapp.models.FilterByUserGenderObj
import com.buzuriu.dogapp.models.IFilterObj

class FilterItems {
    companion object {
        var filterByTimeItems = arrayListOf<IFilterObj>(
            FilterByTimeObj("Today", false),
            FilterByTimeObj("Tomorrow", false),
            FilterByTimeObj("This week", false),
            FilterByTimeObj("This month", false),
            FilterByTimeObj("Next week", false),
            FilterByTimeObj("Next month", false)
        )
        var filterByDogGenderItems = arrayListOf<IFilterObj>(
            FilterByDogGenderObj("male", false),
            FilterByDogGenderObj("female", false)
        )
        var filterByUserGenderItems = arrayListOf<IFilterObj>(
            FilterByUserGenderObj("male", false),
            FilterByUserGenderObj("female", false)
        )
    }
}