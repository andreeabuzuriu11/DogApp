package com.buzuriu.dogapp.viewModels

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.adapters.ParticipantAdapter
import com.buzuriu.dogapp.models.*
import com.buzuriu.dogapp.utils.MapUtils
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PastMeetingDetailViewModel : BaseViewModel() {

    private var participantsList = ArrayList<ParticipantObj>()

    var dogPlaceHolder: MutableLiveData<Drawable>
    var pastMeeting = MutableLiveData<MyCustomMeetingObj>()
    var myLatLng = MutableLiveData<LatLng>()
    var participantsAdapter: ParticipantAdapter? = ParticipantAdapter(participantsList)
    var displayedText = MutableLiveData("")

    init {
        pastMeeting.value =
            exchangeInfoService.get<MyCustomMeetingObj>(this::class.java.name)!!

        displayedText.value =
            "There were no other participants besides you and ${pastMeeting.value!!.user!!.name}"

        dogPlaceHolder = MutableLiveData<Drawable>(getDogPlaceHolder())
        myLatLng.value =
            MapUtils.getLatLngFromGeoPoint(pastMeeting.value!!.meetingObj?.location!!)

        viewModelScope.launch {
            fetchAllParticipantsForMeeting()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private suspend fun fetchAllParticipantsForMeeting() {
        showLoadingView(true)
        viewModelScope.launch(Dispatchers.IO) {
            val list = fetchAllParticipantsForMeetingFromDatabase()
            showLoadingView(false)
            viewModelScope.launch(Dispatchers.Main) {
                participantsList.clear()
                if (!list.isNullOrEmpty()) {
                    displayedText.value = "Other participants"
                }
                participantsList.addAll(list)
                participantsAdapter!!.notifyDataSetChanged()
            }
        }
    }

    private suspend fun fetchAllParticipantsForMeetingFromDatabase(): ArrayList<ParticipantObj> {
        var user: UserObj?
        var dog: DogObj?
        val allParticipantsNameList = ArrayList<ParticipantObj>()

        val allParticipantsList: ArrayList<ParticipantObj>? =
            databaseService.fetchAllMeetingParticipants(pastMeeting.value!!.meetingObj!!.uid!!)

        if (allParticipantsList != null) {
            for (participant in allParticipantsList) {
                user = databaseService.fetchUserByUid(participant.userUid!!)
                if (user != null && participant.userUid != currentUser!!.uid) {
                    // check for user to be different of current user because current user
                    // will not be displayed here
                    dog = databaseService.fetchDogByUid(participant.dogUid!!)
                    if (dog != null) {
                        val participantObj = ParticipantObj(user.name!!, dog.name)
                        allParticipantsNameList.add(participantObj)
                    }
                }
            }
        }

        return allParticipantsNameList
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun getDogPlaceHolder(): Drawable? {
        return activityService.activity!!.getDrawable(R.drawable.ic_dog_svgrepo_com)
    }
}