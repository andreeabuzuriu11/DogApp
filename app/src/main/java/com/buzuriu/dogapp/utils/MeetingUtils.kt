package com.buzuriu.dogapp.utils

import com.buzuriu.dogapp.models.*
import com.google.android.gms.maps.model.LatLng

class MeetingUtils {

    companion object {

        fun checkFiltersAreAllAccomplished(
            meetingObj: MeetingObj,
            filterList: ArrayList<IFilterObj>,
            userLocation: LatLng?
        )
                : Boolean {
            return dogGenderAccepted(meetingObj, filterList) &&
                    userGenderAccepted(meetingObj, filterList) &&
                    timeTypeAccepted(meetingObj, filterList) &&
                    breedTypeAccepted(meetingObj, filterList) &&
                    distanceAccepted(meetingObj, filterList, userLocation)
        }

        private fun dogGenderAccepted(meetingObj: MeetingObj, filterList: ArrayList<IFilterObj>): Boolean {
            val dogGenderFilter = checkFilterIsType<FilterByDogGenderObj>(filterList) ?: return true

            if (meetingObj.dogGender == dogGenderFilter.name)
                return true

            return false
        }

        private fun userGenderAccepted(meetingObj: MeetingObj, filterList: ArrayList<IFilterObj>): Boolean {
            val userGenderFilter = checkFilterIsType<FilterByUserGenderObj>(filterList) ?: return true

            if (meetingObj.userGender == userGenderFilter.name)
                return true

            return false
        }

        private fun timeTypeAccepted(meetingObj: MeetingObj, filterList: ArrayList<IFilterObj>): Boolean {
            val timeFilter = checkFilterIsType<FilterByTimeObj>(filterList) ?: return true

            if (DateUtils.isMeetingHappeningAtThisTime(meetingObj, timeFilter))
                return true

            return false
        }

        private fun breedTypeAccepted(meetingObj: MeetingObj, filterList: ArrayList<IFilterObj>): Boolean {
            val breedFilter = checkFilterIsType<FilterByDogBreedObj>(filterList) ?: return true

            if (meetingObj.dogBreed == breedFilter.name)
                return true

            return false
        }

        private fun distanceAccepted(meetingObj: MeetingObj, filterList: ArrayList<IFilterObj>,
                                     userLocation: LatLng?) : Boolean
        {
            if(userLocation==null) return true

            val filterType = checkFilterIsType<FilterByLocationObj>(filterList)
            if (filterType == null) return true

            val meetingCoords =
                MapUtils.getLatLng(meetingObj.location!!.latitude, meetingObj.location!!.longitude)

            if (MapUtils.getDistanceBetweenCoords(
                    userLocation,
                    meetingCoords) <= (filterType as FilterByLocationObj).distance!!
            ) {
                return true
            }

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