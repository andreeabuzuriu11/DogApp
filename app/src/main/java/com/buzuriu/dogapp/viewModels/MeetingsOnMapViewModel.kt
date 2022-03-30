package com.buzuriu.dogapp.viewModels

import com.google.android.gms.maps.model.LatLng

class MeetingsOnMapViewModel : BaseViewModel() {

    var locationMeetings  = ArrayList<LatLng>()

    init {
        // locationMeetings = dataExchangeService.get<ArrayList<LatLng>>(this::class.java.name)!!
    }

    fun close()
    {
        navigationService.closeCurrentActivity()
    }
}