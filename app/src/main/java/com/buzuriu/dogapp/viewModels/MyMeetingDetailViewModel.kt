package com.buzuriu.dogapp.viewModels

import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import com.buzuriu.dogapp.utils.MapUtils
import com.buzuriu.dogapp.views.AddMeetingActivity
import com.buzuriu.dogapp.views.EditMeetingActivity
import com.buzuriu.dogapp.views.main.ui.my_dogs.MyDogsViewModel
import com.buzuriu.dogapp.views.main.ui.my_meetings.MyMeetingsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MyMeetingDetailViewModel : BaseViewModel() {
    var myCustomMeetingObj = MutableLiveData<MyCustomMeetingObj>()
    var myLatLng = MutableLiveData<LatLng>()

    init {
        myCustomMeetingObj.value = dataExchangeService.get<MyCustomMeetingObj>(this::class.java.name)!!
        myLatLng.value = MapUtils.getLatLngFromGeoPoint(myCustomMeetingObj.value?.meetingObj?.location!!)
    }

    override fun onResume()
    {
        var editedMeeting: MyCustomMeetingObj? =
            dataExchangeService.get<MyCustomMeetingObj>(this::class.java.name) ?: return
        myCustomMeetingObj.value = editedMeeting!!
        editOldMeeting()
        myLatLng.value = MapUtils.getLatLngFromGeoPoint(editedMeeting.meetingObj?.location!!)
        dataExchangeService.put(MyMeetingsViewModel::class.java.name!!,true)

    }

    fun editOldMeeting()
    {
        var myMeetingsList = localDatabaseService.get<ArrayList<MyCustomMeetingObj>>("localMeetingsList")
        if (myMeetingsList == null) return
        if(myMeetingsList.any { it.meetingObj!!.uid == myCustomMeetingObj.value!!.meetingObj!!.uid})
        {
            val oldMeeting = myMeetingsList.find { it.meetingObj!!.uid  == myCustomMeetingObj.value!!.meetingObj!!.uid}
            myMeetingsList.remove(oldMeeting)
        }
        myMeetingsList.add(myCustomMeetingObj.value!!)
        localDatabaseService.add("localMeetingsList", myMeetingsList)
    }

    fun editMeeting()
    {
        dataExchangeService.put(EditMeetingViewModel::class.java.name, myCustomMeetingObj.value!!)
        navigationService.navigateToActivity(EditMeetingActivity::class.java)
    }

    fun deleteMeeting()
    {

    }

}