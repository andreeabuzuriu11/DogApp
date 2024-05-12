package com.buzuriu.dogapp.utils

import com.buzuriu.dogapp.models.*
import java.util.*
import java.util.concurrent.TimeUnit

class FilterItems {

    companion object {


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

        )

        fun setFilterByDogTemperament(temperament: String) {
//            if (filterByDogTemperament.count() == 1)
//                filterByDogTemperament.removeAt(0)
            if (filterByDogTemperament.size>0)
                filterByDogTemperament.removeAll(filterByDogTemperament.toSet())

            filterByDogTemperament.add(FilterByDogTemperamentObj(temperament, true))
        }

    }

}