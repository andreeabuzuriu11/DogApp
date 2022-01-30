package com.buzuriu.dogapp.viewModels

import android.annotation.SuppressLint
import android.content.ReceiverCallNotAllowedException
import android.graphics.drawable.Drawable
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.BreedObj
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.models.MeetingObj
import com.buzuriu.dogapp.utils.ImageUtils
import com.buzuriu.dogapp.utils.StringUtils
import com.buzuriu.dogapp.views.SelectDogFragment
import com.buzuriu.dogapp.views.main.ui.OverlayActivity
import com.buzuriu.dogapp.views.main.ui.dashboard.DashboardViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.invoke.MethodType
import java.util.*

class AddMeetingViewModel : BaseViewModel() {

    var dog = MutableLiveData<DogObj>()
    var dogPlaceHolder: MutableLiveData<Drawable>
    var datePickerCalendar = MutableLiveData<Calendar>()
    var timePickerCalendar = MutableLiveData<Calendar>()
    var position = MutableLiveData<LatLng>()
    var meetingInUtc = Calendar.getInstance()
    var location = GeoPoint(0.0,0.0)

    init {
        dogPlaceHolder = MutableLiveData<Drawable>(getDogPlaceHolder())
        datePickerCalendar.value = Calendar.getInstance()
        timePickerCalendar.value = Calendar.getInstance()
    }

    fun selectDog() {
        navigationService.showOverlay(
            OverlayActivity::class.java,
            false,
            OverlayActivity.fragmentClassNameParam,
            SelectDogFragment::class.qualifiedName
        )
        if (!dog.value?.name.isNullOrEmpty())
            dataExchangeService.put(
                SelectDogViewModel::class.qualifiedName!!,
                dog.value?.name.toString()
            )
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

    private fun getCoordinate()
    {
        val latitude = position.value!!.latitude
        val longitude = position.value!!.longitude

        location = GeoPoint(latitude, longitude)
    }

    fun createMeeting()
    {
        getDateAndTimeOfMeeting()
        getCoordinate()

        if(!isDogSelected())
            return

        var meetingUid = StringUtils.getRandomUID()

        ShowLoadingView(true)

        val newMeeting = MeetingObj(meetingUid, meetingInUtc.timeInMillis, location, dog.value!!.uid, currentUser!!.uid)
        viewModelScope.launch(Dispatchers.IO) {

            databaseService.storeMeetingInfo(meetingUid, newMeeting, object : IOnCompleteListener {
                override fun onComplete(successful: Boolean, exception: Exception?) {

                    if (successful) {
                        viewModelScope.launch(Dispatchers.Main) {
                            dialogService.showSnackbar(R.string.added_success_message_meeting)
                            delay(2000)
                            navigationService.closeCurrentActivity()
                        }
                    } else {
                        viewModelScope.launch(Dispatchers.Main) {
                            if (!exception?.message.isNullOrEmpty())
                                dialogService.showSnackbar(exception!!.message!!)
                            else dialogService.showSnackbar(R.string.unknown_error)
                            delay(2000)
                        }
                    }

                    ShowLoadingView(false)
                }
            })
        }
/*
        val userUid = currentUser!!.uid
        val dogUid = dog.value!!.uid
        val dogName = dog.value!!.name*/
    }

    override fun onResume() {
        val selectedDog = dataExchangeService.get<DogObj>(this::class.qualifiedName!!)
        if (selectedDog != null) {
            dog.value = selectedDog!!
        }
    }

    fun isDogSelected() : Boolean
    {
        if(dog.value == null)
        {
            dialogService.showSnackbar("Please select a dog first!", Snackbar.LENGTH_LONG)
            return false
        }
        return true
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun getDogPlaceHolder(): Drawable? {
        return activityService.activity!!.getDrawable(R.drawable.ic_dog_svgrepo_com)
    }
}