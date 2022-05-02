package com.buzuriu.dogapp.viewModels

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.adapters.ParticipantAdapter
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import com.buzuriu.dogapp.models.ParticipantObj
import com.buzuriu.dogapp.models.UserInfo
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

    init {
        pastMeeting.value =
            dataExchangeService.get<MyCustomMeetingObj>(this::class.java.name)!!
        dogPlaceHolder = MutableLiveData<Drawable>(getDogPlaceHolder())
        myLatLng.value =
            MapUtils.getLatLngFromGeoPoint(pastMeeting.value!!.meetingObj?.location!!)

        viewModelScope.launch {
            fetchAllParticipantsForMeeting()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private suspend fun fetchAllParticipantsForMeeting() {
        ShowLoadingView(true)
        viewModelScope.launch(Dispatchers.IO) {
            val list = fetchAllParticipantsForMeetingFromDatabase()
            ShowLoadingView(false)
            viewModelScope.launch(Dispatchers.Main) {
                participantsList.clear()
                participantsList.addAll(list)
                participantsAdapter!!.notifyDataSetChanged()
            }
        }
    }

    private suspend fun fetchAllParticipantsForMeetingFromDatabase(): ArrayList<ParticipantObj> {
        var user: UserInfo?
        var dog: DogObj?
        val allParticipantsNameList = ArrayList<ParticipantObj>()

        val allParticipantsList: ArrayList<ParticipantObj>? =
            databaseService.fetchAllMeetingParticipants(pastMeeting.value!!.meetingObj!!.uid!!)

        if (allParticipantsList != null) {
            for (participant in allParticipantsList) {
                user = databaseService.fetchUserByUid(participant.userUid!!)
                dog = databaseService.fetchDogByUid(participant.dogUid!!)
                if (dog != null) {
                    val participantObj = ParticipantObj(user!!.name!!, dog.name)
                    allParticipantsNameList.add(participantObj)
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