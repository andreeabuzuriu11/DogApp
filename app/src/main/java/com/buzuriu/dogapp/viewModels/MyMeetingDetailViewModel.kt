package com.buzuriu.dogapp.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.adapters.ParticipantAdapter
import com.buzuriu.dogapp.listeners.IClickListener
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import com.buzuriu.dogapp.models.ParticipantObj
import com.buzuriu.dogapp.models.UserInfo
import com.buzuriu.dogapp.utils.MapUtils
import com.buzuriu.dogapp.views.EditMeetingActivity
import com.buzuriu.dogapp.views.main.ui.my_meetings.MyMeetingsViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MyMeetingDetailViewModel : BaseViewModel() {
    var participantsList = ArrayList<ParticipantObj>()
    var participantsAdapter: ParticipantAdapter? = ParticipantAdapter(participantsList)
    var myCustomMeetingObj = MutableLiveData<MyCustomMeetingObj>()

    var myLatLng = MutableLiveData<LatLng>()

    init {
        myCustomMeetingObj.value =
            dataExchangeService.get<MyCustomMeetingObj>(this::class.java.name)!!
        myLatLng.value =
            MapUtils.getLatLngFromGeoPoint(myCustomMeetingObj.value?.meetingObj?.location!!)


        viewModelScope.launch {
            fetchAllParticipantsForMeeting()
        }
    }

    override fun onResume() {
        val editedMeeting: MyCustomMeetingObj =
            dataExchangeService.get<MyCustomMeetingObj>(this::class.java.name) ?: return
        myCustomMeetingObj.value = editedMeeting
        editOldMeeting()
        myLatLng.value = MapUtils.getLatLngFromGeoPoint(editedMeeting.meetingObj?.location!!)
        dataExchangeService.put(MyMeetingsViewModel::class.java.name, true)
    }

    fun editMeeting() {
        dataExchangeService.put(EditMeetingViewModel::class.java.name, myCustomMeetingObj.value!!)
        navigationService.navigateToActivity(EditMeetingActivity::class.java)
    }

    fun deleteMeeting() {
        dialogService.showAlertDialog(
            "Delete meeting?",
            "Are you sure you want to delete meeting with ${myCustomMeetingObj.value!!.dog!!.name}? This action cannot be undone.",
            "Yes, delete it",
            object :
                IClickListener {
                override fun clicked() {
                    deleteMeetingFromDatabase()
                    dataExchangeService.put(
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
                            localDatabaseService.get<ArrayList<MyCustomMeetingObj>>("localMeetingsList")
                        allMyMeetingsList!!.remove<MyCustomMeetingObj>(myCustomMeetingObj.value!!)
                        localDatabaseService.add("localMeetingsList", allMyMeetingsList)
                        navigationService.closeCurrentActivity()
                    }
                })
        }
    }

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
            databaseService.fetchAllMeetingParticipants(myCustomMeetingObj.value!!.meetingObj!!.uid!!)

        if (allParticipantsList != null) {
            for (participant in allParticipantsList) {
                user = databaseService.fetchUserByUid(participant.userUid!!)
                dog = databaseService.fetchDogByUid(participant.dogUid!!)
                val participantObj = ParticipantObj(user!!.name!!, dog!!.name)

                allParticipantsNameList.add(participantObj)
            }
        }

        return allParticipantsNameList
    }

    private fun editOldMeeting() {
        val myMeetingsList =
            localDatabaseService.get<ArrayList<MyCustomMeetingObj>>("localMeetingsList")
                ?: return
        if (myMeetingsList.any { it.meetingObj!!.uid == myCustomMeetingObj.value!!.meetingObj!!.uid }) {
            val oldMeeting =
                myMeetingsList.find { it.meetingObj!!.uid == myCustomMeetingObj.value!!.meetingObj!!.uid }
            myMeetingsList.remove(oldMeeting)
        }
        myMeetingsList.add(myCustomMeetingObj.value!!)
        localDatabaseService.add("localMeetingsList", myMeetingsList)
    }
}