package com.buzuriu.dogapp.utils

import com.buzuriu.dogapp.models.*
import com.buzuriu.dogapp.services.LocalDatabaseService
import com.google.android.gms.maps.model.LatLng
import java.util.*
import kotlin.collections.ArrayList

class MeetingUtils {

    companion object {

        fun checkFiltersAreAllAccomplished(
            meetingObj: MeetingObj,
            filterList: ArrayList<IFilterObj>,
            userLocation: LatLng?, dogPersonality: DogPersonality? = null
        )
                : Boolean {
            return dogGenderAccepted(meetingObj, filterList) &&
                    userGenderAccepted(meetingObj, filterList) &&
                    timeTypeAccepted(meetingObj, filterList) &&
                    breedTypeAccepted(meetingObj, filterList) &&
                    temperamentTypeAccepted(filterList, dogPersonality!!) &&
                    energyLevelAccepted(filterList, dogPersonality!!) &&
                    distanceAccepted(meetingObj, filterList, userLocation)
        }

        private fun dogGenderAccepted(
            meetingObj: MeetingObj,
            filterList: ArrayList<IFilterObj>
        ): Boolean {
            val dogGenderFilter = checkFilterIsType<FilterByDogGenderObj>(filterList) ?: return true

            if (meetingObj.dogGender == dogGenderFilter.name)
                return true

            return false
        }

        private fun userGenderAccepted(
            meetingObj: MeetingObj,
            filterList: ArrayList<IFilterObj>
        ): Boolean {
            val userGenderFilter =
                checkFilterIsType<FilterByUserGenderObj>(filterList) ?: return true

            if (meetingObj.userGender == userGenderFilter.name)
                return true

            return false
        }

        private fun timeTypeAccepted(
            meetingObj: MeetingObj,
            filterList: ArrayList<IFilterObj>
        ): Boolean {
            val timeFilter = checkFilterIsType<FilterByTimeObj>(filterList) ?: return true

            if (DateUtils.isMeetingHappeningAtThisTime(meetingObj, timeFilter))
                return true

            return false
        }
        private fun temperamentTypeAccepted(
            filterList: ArrayList<IFilterObj>,
            dogPersonality: DogPersonality
        ): Boolean {
            val temperamentFilter = checkFilterIsType<FilterByDogTemperamentObj>(filterList) ?: return true

            if (temperamentFilter.name == "All")
                return true

            if (dogPersonality.temperament.contains(temperamentFilter.name.toString()))
                return true

            return false
        }

        private fun energyLevelAccepted(
            filterList: ArrayList<IFilterObj>,
            dogPersonality: DogPersonality
        ): Boolean {
            val energyFilter = checkFilterIsType<FilterByDogEnergyLevelObj>(filterList) ?: return true

            if (energyFilter.name == "All")
                return true

            if (dogPersonality.energy_level_category.contains(energyFilter.name.toString()))
                return true

            return false
        }

        fun isMeetingInThePast(meetingObj: MeetingObj): Boolean {
            val now = Calendar.getInstance()
            val meetingCalendar = Calendar.getInstance()

            now.timeInMillis = System.currentTimeMillis()
            meetingCalendar.timeInMillis = meetingObj.date!!

            if (now.after(meetingCalendar))
                return true
            return false
        }

        private fun breedTypeAccepted(
            meetingObj: MeetingObj,
            filterList: ArrayList<IFilterObj>
        ): Boolean {
            val breedFilter = checkFilterIsType<FilterByDogBreedObj>(filterList) ?: return true

            if (meetingObj.dogBreed == breedFilter.name)
                return true

            return false
        }

        private fun distanceAccepted(
            meetingObj: MeetingObj, filterList: ArrayList<IFilterObj>,
            userLocation: LatLng?
        ): Boolean {
            if (userLocation == null) return true

            val filterType = checkFilterIsType<FilterByLocationObj>(filterList) ?: return true

            val meetingCoordinates =
                MapUtils.getLatLng(meetingObj.location!!.latitude, meetingObj.location!!.longitude)

            if (MapUtils.getDistanceBetweenCoords(
                    userLocation,
                    meetingCoordinates
                ) <= filterType.distance!!
            ) {
                return true
            }

            return false

        }

        private inline fun <reified T> checkFilterIsType(filterList: ArrayList<IFilterObj>): T? {
            for (filter in filterList) {
                if (filter is T)
                    return filter
            }
            return null
        }

    }
}