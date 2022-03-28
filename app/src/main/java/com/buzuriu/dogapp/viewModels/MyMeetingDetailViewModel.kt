package com.buzuriu.dogapp.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.listeners.IClickListener
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import com.buzuriu.dogapp.utils.MapUtils
import com.buzuriu.dogapp.views.EditMeetingActivity
import com.buzuriu.dogapp.views.main.ui.my_meetings.MyMeetingsViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception


class MyMeetingDetailViewModel : BaseViewModel() {
    var myCustomMeetingObj = MutableLiveData<MyCustomMeetingObj>()
    var myLatLng = MutableLiveData<LatLng>()

    init {
        myCustomMeetingObj.value = dataExchangeService.get<MyCustomMeetingObj>(this::class.java.name)!!
        myLatLng.value = MapUtils.getLatLngFromGeoPoint(myCustomMeetingObj.value?.meetingObj?.location!!)
    }

    override fun onResume()
    {
        val editedMeeting: MyCustomMeetingObj =
            dataExchangeService.get<MyCustomMeetingObj>(this::class.java.name) ?: return
        myCustomMeetingObj.value = editedMeeting
        editOldMeeting()
        myLatLng.value = MapUtils.getLatLngFromGeoPoint(editedMeeting.meetingObj?.location!!)
        dataExchangeService.put(MyMeetingsViewModel::class.java.name,true)
    }

    private fun editOldMeeting()
    {
        val myMeetingsList = localDatabaseService.get<ArrayList<MyCustomMeetingObj>>("localMeetingsList")
            ?: return
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
        dialogService.showAlertDialog("Delete meeting?", "Are you sure you want to delete meeting with ${myCustomMeetingObj.value!!.dog!!.name}? This action cannot be undone.", "Yes, delete it", object :
            IClickListener {
            override fun clicked() {
                deleteMeetingFromDatabase()
                dataExchangeService.put(MyMeetingsViewModel::class.java.name, true) // is refresh list needed
            }
        })
    }

    fun deleteMeetingFromDatabase()
    {
        viewModelScope.launch(Dispatchers.IO) {
            databaseService.deleteMeeting(myCustomMeetingObj.value!!.meetingObj!!.uid!!, object : IOnCompleteListener
            {
                override fun onComplete(successful: Boolean, exception: Exception?) {
                        val allMyMeetingsList =
                            localDatabaseService.get<ArrayList<MyCustomMeetingObj>>("localMeetingsList")
                        allMyMeetingsList!!.remove<MyCustomMeetingObj>(myCustomMeetingObj.value!!)
                        localDatabaseService.add("localMeetingsList", allMyMeetingsList)
                        navigationService.closeCurrentActivity()
                }
            })
        }
    }
}