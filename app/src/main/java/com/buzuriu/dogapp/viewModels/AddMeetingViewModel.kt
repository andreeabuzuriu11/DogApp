package com.buzuriu.dogapp.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.*
import com.buzuriu.dogapp.utils.LocalDBItems
import com.buzuriu.dogapp.utils.StringUtils
import com.buzuriu.dogapp.views.SelectDogFragment
import com.buzuriu.dogapp.views.main.ui.OverlayActivity
import com.buzuriu.dogapp.views.main.ui.my_meetings.MyMeetingsViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class AddMeetingViewModel : BaseViewModel() {

    private var meetingInUtc: Calendar = Calendar.getInstance()

    var dog = MutableLiveData<DogObj>()
    var datePickerCalendar = MutableLiveData<Calendar>()
    var timePickerCalendar = MutableLiveData<Calendar>()
    var position = MutableLiveData<LatLng>()
    var location = GeoPoint(0.0, 0.0)

    init {
        datePickerCalendar.value = Calendar.getInstance()
        timePickerCalendar.value = Calendar.getInstance()

        if (doesUserHaveOnlyOneDog())
            dog.value = localDatabaseService.get<ArrayList<DogObj>>(LocalDBItems.localDogsList)?.get(0)
    }

    override fun onResume() {
        val selectedDog = exchangeInfoService.get<DogObj>(this::class.qualifiedName!!)
        if (selectedDog != null) {
            dog.value = selectedDog!!
        }
    }

    fun selectDog() {
        navigationService.showOverlay(
            OverlayActivity::class.java,
            false,
            LocalDBItems.fragmentName,
            SelectDogFragment::class.qualifiedName
        )
        if (!dog.value?.name.isNullOrEmpty())
            exchangeInfoService.put(
                SelectDogViewModel::class.qualifiedName!!,
                dog.value?.name.toString()
            )
    }

    fun createMeeting() {
        getDateAndTimeOfMeeting()
        getCoordinate()

        if (!isDogSelected())
            return

        val userGender = localDatabaseService.get<UserObj>(LocalDBItems.currentUser)!!.gender

        val meetingUid = StringUtils.getRandomUID()

        showLoadingView(true)

        val newMeeting = MeetingObj(
            meetingUid, meetingInUtc.timeInMillis, location, dog.value!!.uid, currentUser!!.uid,
            dog.value!!.gender, dog.value!!.breed, userGender!!
        )

        viewModelScope.launch(Dispatchers.IO) {

            databaseService.storeMeeting(meetingUid, newMeeting, object : IOnCompleteListener {
                override fun onComplete(successful: Boolean, exception: Exception?) {

                    if (successful) {
                        viewModelScope.launch(Dispatchers.Main) {
                            snackMessageService.displaySnackBar(R.string.added_success_message_meeting)
                            exchangeInfoService.put(MyMeetingsViewModel::class.java.name, true)
                            addMeetingToLocalDatabase(newMeeting)
                            delay(2000)
                            firebaseAnalyticsService.logEvent("Event", "MeetingEvent", "Create_Meeting")
                            navigationService.closeCurrentActivity()
                        }
                    } else {
                        viewModelScope.launch(Dispatchers.Main) {
                            if (!exception?.message.isNullOrEmpty())
                                snackMessageService.displaySnackBar(exception!!.message!!)
                            else snackMessageService.displaySnackBar(R.string.unknown_error)
                            delay(2000)
                        }
                    }
                    showLoadingView(false)
                }
            })
        }
    }

    private fun addMeetingToLocalDatabase(meetingObj: MeetingObj) {
        val user = localDatabaseService.get<UserObj>(LocalDBItems.currentUser)!!
        val myCustomMeetingObj = MyCustomMeetingObj(meetingObj, user, dog.value!!)

        val myMeetingsList =
            localDatabaseService.get<ArrayList<MyCustomMeetingObj>>(LocalDBItems.localMeetingsList)
                ?: return
        myMeetingsList.add(myCustomMeetingObj)
        localDatabaseService.add(LocalDBItems.localMeetingsList, myMeetingsList)
    }

    private fun getDateAndTimeOfMeeting() {
        val year = datePickerCalendar.value!!.get(Calendar.YEAR)
        val month = datePickerCalendar.value!!.get(Calendar.MONTH)
        val day = datePickerCalendar.value!!.get(Calendar.DAY_OF_MONTH)
        val hour = timePickerCalendar.value!!.get(Calendar.HOUR_OF_DAY)
        val minute = timePickerCalendar.value!!.get(Calendar.MINUTE)

        meetingInUtc.set(year, month, day, hour, minute)
    }

    private fun getCoordinate() {
        val latitude = position.value!!.latitude
        val longitude = position.value!!.longitude

        location = GeoPoint(latitude, longitude)
    }

    private fun isDogSelected(): Boolean {
        if (dog.value == null) {
            snackMessageService.displaySnackBar("Please select a dog first!", Snackbar.LENGTH_LONG)
            return false
        }
        return true
    }

    private fun doesUserHaveOnlyOneDog(): Boolean {
        if (localDatabaseService.get<ArrayList<DogObj>>(LocalDBItems.localDogsList)!!.size == 1)
            return true
        return false
    }
}