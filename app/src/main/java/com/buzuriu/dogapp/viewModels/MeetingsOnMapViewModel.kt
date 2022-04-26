package com.buzuriu.dogapp.viewModels

import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.models.FilterByLocationObj
import com.buzuriu.dogapp.models.SharedPreferences
import com.buzuriu.dogapp.views.main.ui.map.MapViewModel
import com.google.android.gms.maps.model.LatLng

class MeetingsOnMapViewModel : BaseViewModel() {

    var locationMeetings: ArrayList<LatLng>? = ArrayList<LatLng>()
    var progress = MutableLiveData<Int>(0)
    var userCoordinates = MutableLiveData<LatLng>()

    init {
        locationMeetings = localDatabaseService.get<ArrayList<LatLng>>("locationPoints")
    }

    fun search() {
        var meetingString = progress.value.toString() + " km"
        val mapFilter = FilterByLocationObj(meetingString, progress.value!!, true)
        dataExchangeService.put(MapViewModel::class.qualifiedName!!, mapFilter)

        val myUserCoords = userCoordinates.value
        sharedPreferenceService
            .writeInSharedPref(SharedPreferences.userLocationKey, myUserCoords!!)

        navigationService.closeCurrentActivity()
    }

    fun close() {
        navigationService.closeCurrentActivity()
    }


}