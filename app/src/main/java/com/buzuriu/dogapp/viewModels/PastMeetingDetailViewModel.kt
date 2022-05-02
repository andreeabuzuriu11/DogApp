package com.buzuriu.dogapp.viewModels

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import com.buzuriu.dogapp.utils.MapUtils
import com.google.android.gms.maps.model.LatLng

class PastMeetingDetailViewModel : BaseViewModel() {

    var dogPlaceHolder: MutableLiveData<Drawable>
    var pastMeeting = MutableLiveData<MyCustomMeetingObj>()
    var myLatLng = MutableLiveData<LatLng>()

    init {
        pastMeeting.value =
            dataExchangeService.get<MyCustomMeetingObj>(this::class.java.name)!!
        dogPlaceHolder = MutableLiveData<Drawable>(getDogPlaceHolder())
        myLatLng.value =
            MapUtils.getLatLngFromGeoPoint(pastMeeting.value!!.meetingObj?.location!!)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun getDogPlaceHolder(): Drawable? {
        return activityService.activity!!.getDrawable(R.drawable.ic_dog_svgrepo_com)
    }
}