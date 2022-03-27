package com.buzuriu.dogapp.viewModels

import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import com.buzuriu.dogapp.utils.MapUtils
import com.google.android.gms.maps.model.LatLng
import java.util.*

class EditMeetingViewModel : BaseViewModel() {
    var myCustomMeetingObj = MutableLiveData<MyCustomMeetingObj>()
    var calendar = MutableLiveData<Calendar>()
    var myLatLng = MutableLiveData<LatLng>()


    init {
        myCustomMeetingObj.value = dataExchangeService.get<MyCustomMeetingObj>(this::class.java.name)
        var time: Long? = myCustomMeetingObj.value?.meetingObj!!.date!!
        calendar.value = longToCalendar(time)!!
        myLatLng.value = MapUtils.getLatLngFromGeoPoint(myCustomMeetingObj.value?.meetingObj?.location!!)
    }

    fun longToCalendar(time : Long?) : Calendar?
    {
        var c : Calendar ?= null
        if (time!=null)
        {
            c = Calendar.getInstance()
            c.timeInMillis = time
        }
        return c
    }

}