package com.buzuriu.dogapp.utils

import com.buzuriu.dogapp.models.FilterByDogGenderObj
import com.buzuriu.dogapp.models.FilterByTimeObj
import com.buzuriu.dogapp.models.IFilterObj
import com.buzuriu.dogapp.models.MeetingObj

class MeetingUtils {

    companion object {

        fun checkFiltersAreAllAccomplished(
            meetingObj: MeetingObj,
            filterList: ArrayList<IFilterObj>
        )
                : Boolean {
            return dogGenderAccepted(meetingObj, filterList) &&
                    timeTypeAccepted(meetingObj, filterList)
        }

        fun dogGenderAccepted(meetingObj: MeetingObj, filterList: ArrayList<IFilterObj>): Boolean {
            val dogGenderFilter = checkFilterIsType<FilterByDogGenderObj>(filterList)
            if (dogGenderFilter == null)
                return true

            if (meetingObj.dogGender == dogGenderFilter.name)
                return true

            return false
        }

        fun timeTypeAccepted(meetingObj: MeetingObj, filterList: ArrayList<IFilterObj>): Boolean {
            val timeFilter = checkFilterIsType<FilterByTimeObj>(filterList)
            if (timeFilter == null)
                return true

            if (DateUtils.isMeetingHappeningAtThisTime(meetingObj, timeFilter))
                return true

            return false
        }


        inline fun <reified T>checkFilterIsType(filterList: ArrayList<IFilterObj>): T? {
            for (filter in filterList) {
                if (filter is T)
                    return filter
            }
            return null
        }

    }
}