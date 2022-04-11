package com.buzuriu.dogapp.viewModels

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng

class MeetingsOnMapViewModel : BaseViewModel() {

    var locationMeetings : ArrayList<LatLng>? = ArrayList<LatLng>()
    var progress = MutableLiveData<Int>(0)

    init {
        locationMeetings = localDatabaseService.get<ArrayList<LatLng>>("locationPoints")
    }

    fun close()
    {
        navigationService.closeCurrentActivity()
    }
}