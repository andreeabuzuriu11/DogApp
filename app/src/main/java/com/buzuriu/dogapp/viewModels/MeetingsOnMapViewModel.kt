package com.buzuriu.dogapp.viewModels

import com.google.android.gms.maps.model.LatLng

class MeetingsOnMapViewModel : BaseViewModel() {

    var locationMeetings : ArrayList<LatLng>? = ArrayList<LatLng>()

    init {
        locationMeetings = localDatabaseService.get<ArrayList<LatLng>>("locationPoints")
    }

    fun close()
    {
        navigationService.closeCurrentActivity()
    }
}