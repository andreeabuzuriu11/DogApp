package com.buzuriu.dogapp.utils

import com.buzuriu.dogapp.models.*

class MeetingUtils {

    companion object {

        fun checkFiltersAreAllAccomplished(
            meetingObj: MeetingObj,
            filterList: ArrayList<IFilterObj>
        )
                : Boolean {
            return dogGenderAccepted(meetingObj, filterList) &&
                    timeTypeAccepted(meetingObj, filterList) &&
                    breedTypeAccepted(meetingObj, filterList)
        }

        private fun dogGenderAccepted(meetingObj: MeetingObj, filterList: ArrayList<IFilterObj>): Boolean {
            val dogGenderFilter = checkFilterIsType<FilterByDogGenderObj>(filterList)
            if (dogGenderFilter == null)
                return true

            if (meetingObj.dogGender == dogGenderFilter.name)
                return true

            return false
        }

        private fun timeTypeAccepted(meetingObj: MeetingObj, filterList: ArrayList<IFilterObj>): Boolean {
            val timeFilter = checkFilterIsType<FilterByTimeObj>(filterList)
            if (timeFilter == null)
                return true

            if (DateUtils.isMeetingHappeningAtThisTime(meetingObj, timeFilter))
                return true

            return false
        }

        private fun breedTypeAccepted(meetingObj: MeetingObj, filterList: ArrayList<IFilterObj>): Boolean {
            val breedFilter = checkFilterIsType<FilterByDogBreedObj>(filterList)
            if (breedFilter == null)
                return true

            if (meetingObj.dogBreed == breedFilter.name)
                return true

            return false
        }


        private inline fun <reified T>checkFilterIsType(filterList: ArrayList<IFilterObj>): T? {
            for (filter in filterList) {
                if (filter is T)
                    return filter
            }
            return null
        }

    }
}