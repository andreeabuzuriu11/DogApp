package com.buzuriu.dogapp.utils

import com.buzuriu.dogapp.models.*

class FilterItems {

    companion object {
        var temperament = ""

        var filterByTimeItems = arrayListOf<IFilterObj>(
            FilterByTimeObj("Today", false),
            FilterByTimeObj("Tomorrow", false),
            FilterByTimeObj("Next Friday", false),
            FilterByTimeObj("Next Saturday", false),
            FilterByTimeObj("Next Sunday", false)
        )
        var filterByDogGenderItems = arrayListOf<IFilterObj>(
            FilterByDogGenderObj("male", false),
            FilterByDogGenderObj("female", false)
        )
        var filterByUserGenderItems = arrayListOf<IFilterObj>(
            FilterByUserGenderObj("male", false),
            FilterByUserGenderObj("female", false)
        )

        var filterByDogTemperament = arrayListOf<IFilterObj>(
            FilterByDogTemperamentObj(temperament, false)
        )
    }
}