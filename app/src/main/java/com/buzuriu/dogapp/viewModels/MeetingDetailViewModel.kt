package com.buzuriu.dogapp.viewModels

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import com.buzuriu.dogapp.models.ParticipantObj
import com.buzuriu.dogapp.utils.MapUtils
import com.buzuriu.dogapp.views.SelectDogForJoinMeetFragment
import com.buzuriu.dogapp.views.main.ui.OverlayActivity
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class MeetingDetailViewModel : BaseViewModel() {

    var myCustomMeetingObj = MutableLiveData<MyCustomMeetingObj>()
    var myLatLng = MutableLiveData<LatLng>()
    var myDog = MutableLiveData<DogObj>()
    var dogPlaceHolder: MutableLiveData<Drawable>
    var isUserAttending = MutableLiveData<Boolean>()

    init {
        dogPlaceHolder = MutableLiveData<Drawable>(getDogPlaceHolder())

        myCustomMeetingObj.value =
            dataExchangeService.get<MyCustomMeetingObj>(this::class.java.name)
        myLatLng.value =
            MapUtils.getLatLngFromGeoPoint(myCustomMeetingObj.value?.meetingObj?.location!!)

        viewModelScope.launch {
            fetchDogUserAttendsWith()
        }
    }

    override fun onResume() {
        super.onResume()

        val selectedDog = dataExchangeService.get<DogObj>(this::class.qualifiedName!!)
        if (selectedDog != null) {
            myDog.value = selectedDog!!
        }
    }


    private suspend fun fetchDogUserAttendsWith(): DogObj? {
        var dog: DogObj?

        val allParticipantsList: ArrayList<ParticipantObj>? =
            databaseService.fetchAllMeetingParticipants(myCustomMeetingObj.value!!.meetingObj!!.uid!!)

        if (allParticipantsList != null) {
            for (participant in allParticipantsList) {
                if (participant.userUid == currentUser!!.uid) {
                    isUserAttending.value = true
                    dog = databaseService.fetchDogByUid(participant.dogUid!!)
                    if (dog != null) {
                        myDog.value = dog!!
                    }
                } else {
                    isUserAttending.value = false
                }

            }
        }
        return myDog.value
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun getDogPlaceHolder(): Drawable? {
        return activityService.activity!!.getDrawable(R.drawable.ic_dog_svgrepo_com)
    }

    fun changeDog() {
        dataExchangeService.put(
            SelectDogForJoinMeetViewModel::class.java.name,
            myCustomMeetingObj.value!!
        )

        navigationService.showOverlay(
            OverlayActivity::class.java,
            false,
            OverlayActivity.fragmentClassNameParam,
            SelectDogForJoinMeetFragment::class.qualifiedName
        )
    }

}