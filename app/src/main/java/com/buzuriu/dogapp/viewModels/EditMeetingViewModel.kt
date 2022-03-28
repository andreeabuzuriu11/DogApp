package com.buzuriu.dogapp.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.MeetingObj
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import com.buzuriu.dogapp.utils.MapUtils
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

class EditMeetingViewModel : BaseViewModel() {
    var myCustomMeetingObj : MyCustomMeetingObj? = null
    var datePickerCalendar = MutableLiveData<Calendar>()
    var timePickerCalendar = MutableLiveData<Calendar>()
    var myLatLng = MutableLiveData<LatLng>()
    var meetingInUtc = Calendar.getInstance()
    var location = GeoPoint(0.0,0.0)


    init {
        myCustomMeetingObj = dataExchangeService.get<MyCustomMeetingObj>(this::class.java.name)!!
        if (myCustomMeetingObj != null)
            initFields()
    }

    private fun getDateAndTimeOfMeeting()
    {
        val year = datePickerCalendar.value!!.get(Calendar.YEAR)
        val month = datePickerCalendar.value!!.get(Calendar.MONTH)
        val day = datePickerCalendar.value!!.get(Calendar.DAY_OF_MONTH)
        val hour = timePickerCalendar.value!!.get(Calendar.HOUR_OF_DAY)
        val minute = timePickerCalendar.value!!.get(Calendar.MINUTE)

        meetingInUtc.set(year, month, day, hour, minute)
    }

    private fun initFields()
    {
        val time: Long? = myCustomMeetingObj!!.meetingObj!!.date!!
        timePickerCalendar.value = longToCalendar(time)!!
        datePickerCalendar.value = longToCalendar(time)!!
        myLatLng.value = MapUtils.getLatLngFromGeoPoint(myCustomMeetingObj!!.meetingObj?.location!!)
    }

    private fun longToCalendar(time : Long?) : Calendar?
    {
        var c : Calendar ?= null
        if (time!=null)
        {
            c = Calendar.getInstance()
            c.timeInMillis = time
        }
        return c
    }

    private fun getCoordinate()
    {
        val latitude = myLatLng.value!!.latitude
        val longitude = myLatLng.value!!.longitude

        location = GeoPoint(latitude, longitude)
    }


    fun editMeeting()
    {
        getCoordinate()
        getDateAndTimeOfMeeting()
        myCustomMeetingObj!!.meetingObj =  MeetingObj(
            myCustomMeetingObj!!.meetingObj?.uid!!,
            meetingInUtc.timeInMillis,
            location,
            myCustomMeetingObj!!.dog!!.uid,
            currentUser!!.uid,
            myCustomMeetingObj!!.dog!!.gender,
            myCustomMeetingObj!!.dog!!.breed,
            myCustomMeetingObj!!.user!!.gender!!
        )

        viewModelScope.launch(Dispatchers.IO) {
            databaseService.storeMeetingInfo(myCustomMeetingObj!!.meetingObj?.uid!!, myCustomMeetingObj!!.meetingObj!!, object : IOnCompleteListener {
                override fun onComplete(successful: Boolean, exception: Exception?) {
                    if (successful)
                    {
                        viewModelScope.launch(Dispatchers.Main) {
                            dialogService.showSnackbar("Edited successful")
                            delay(2000)
                            navigationService.closeCurrentActivity()
                        }
                    }
                    else
                    {
                        viewModelScope.launch(Dispatchers.Main) {
                            if (!exception?.message.isNullOrEmpty())
                                dialogService.showSnackbar(exception!!.message!!)
                            else dialogService.showSnackbar(R.string.unknown_error)
                        }

                    }
                }
            })

        }
    }

}