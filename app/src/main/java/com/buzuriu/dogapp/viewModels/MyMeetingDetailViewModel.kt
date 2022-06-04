package com.buzuriu.dogapp.viewModels

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.adapters.ParticipantAdapter
import com.buzuriu.dogapp.listeners.IClickListener
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import com.buzuriu.dogapp.models.ParticipantObj
import com.buzuriu.dogapp.models.UserObj
import com.buzuriu.dogapp.utils.LocalDBItems
import com.buzuriu.dogapp.utils.MapUtils
import com.buzuriu.dogapp.utils.MeetingUtils
import com.buzuriu.dogapp.views.EditMeetingActivity
import com.buzuriu.dogapp.views.main.ui.my_meetings.MyMeetingsViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MyMeetingDetailViewModel : BaseViewModel() {
    private var participantsList = ArrayList<ParticipantObj>()
    var participantsAdapter: ParticipantAdapter? = ParticipantAdapter(participantsList)
    var myCustomMeetingObj = MutableLiveData<MyCustomMeetingObj>()
    var myLatLng = MutableLiveData<LatLng>()
    var displayedText = MutableLiveData("There are no participants yet")

    init {
        myCustomMeetingObj.value =
            exchangeInfoService.get<MyCustomMeetingObj>(this::class.java.name)!!
        myLatLng.value =
            MapUtils.getLatLngFromGeoPoint(myCustomMeetingObj.value?.meetingObj?.location!!)

        viewModelScope.launch {
            fetchAllParticipantsForMeeting()
        }
    }

    override fun onResume() {
        val editedMeeting: MyCustomMeetingObj =
            exchangeInfoService.get<MyCustomMeetingObj>(this::class.java.name) ?: return
        myCustomMeetingObj.value = editedMeeting
        editOldMeeting()
        myLatLng.value = MapUtils.getLatLngFromGeoPoint(editedMeeting.meetingObj?.location!!)
        exchangeInfoService.put(MyMeetingsViewModel::class.java.name, true)
    }

    fun editMeeting() {
        exchangeInfoService.put(EditMeetingViewModel::class.java.name, myCustomMeetingObj.value!!)
        navigationService.navigateToActivity(EditMeetingActivity::class.java)
    }

    fun deleteMeeting() {
        alertMessageService.displayAlertDialog(
            "Delete meeting?",
            "Are you sure you want to delete meeting with ${myCustomMeetingObj.value!!.dog!!.name}? This action cannot be undone.",
            "Yes, delete it",
            object :
                IClickListener {
                override fun clicked() {
                    deleteMeetingFromDatabase()
                    exchangeInfoService.put(
                        MyMeetingsViewModel::class.java.name,
                        true
                    ) // is refresh list needed
                }
            })
    }

    fun deleteMeetingFromDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            databaseService.deleteMeeting(
                myCustomMeetingObj.value!!.meetingObj!!.uid!!,
                object : IOnCompleteListener {
                    override fun onComplete(successful: Boolean, exception: Exception?) {
                        val allMyMeetingsList =
                            localDatabaseService.get<ArrayList<MyCustomMeetingObj>>(LocalDBItems.localMeetingsList)
                        allMyMeetingsList!!.remove<MyCustomMeetingObj>(myCustomMeetingObj.value!!)
                        localDatabaseService.add(LocalDBItems.localMeetingsList, allMyMeetingsList)
                        navigationService.closeCurrentActivity()
                    }
                })
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
                if (!list.isNullOrEmpty())
                {
                    displayedText.value = "Participants"
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
            databaseService.fetchAllMeetingParticipants(myCustomMeetingObj.value!!.meetingObj!!.uid!!)

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

    private fun editOldMeeting() {
        val myMeetingsList =
            localDatabaseService.get<ArrayList<MyCustomMeetingObj>>(LocalDBItems.localMeetingsList)
                ?: return
        if (myMeetingsList.any { it.meetingObj!!.uid == myCustomMeetingObj.value!!.meetingObj!!.uid }) {
            val oldMeeting =
                myMeetingsList.find { it.meetingObj!!.uid == myCustomMeetingObj.value!!.meetingObj!!.uid }
            myMeetingsList.remove(oldMeeting)
        }
        myMeetingsList.add(myCustomMeetingObj.value!!)
        localDatabaseService.add(LocalDBItems.localMeetingsList, myMeetingsList)
    }

    fun isMeetingPast() : Boolean
    {
        return (MeetingUtils.isMeetingInThePast(myCustomMeetingObj.value!!.meetingObj!!))
    }
}