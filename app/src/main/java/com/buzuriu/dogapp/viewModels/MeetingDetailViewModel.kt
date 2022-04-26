package com.buzuriu.dogapp.viewModels

import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import com.buzuriu.dogapp.utils.MapUtils
import com.google.android.gms.maps.model.LatLng

class MeetingDetailViewModel : BaseViewModel() {

    var myCustomMeetingObj = MutableLiveData<MyCustomMeetingObj>()
    var myLatLng = MutableLiveData<LatLng>()

    init {
        myCustomMeetingObj.value =
            dataExchangeService.get<MyCustomMeetingObj>(this::class.java.name)
        myLatLng.value =
            MapUtils.getLatLngFromGeoPoint(myCustomMeetingObj.value?.meetingObj?.location!!)
    }

}