package com.buzuriu.dogapp.views.main.ui.my_meetings

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.adapters.MyMeetingAdapter
import com.buzuriu.dogapp.listeners.IClickListener
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.*
import com.buzuriu.dogapp.utils.LocalDBItems
import com.buzuriu.dogapp.utils.MeetingUtils
import com.buzuriu.dogapp.viewModels.BaseViewModel
import com.buzuriu.dogapp.viewModels.MeetingDetailViewModel
import com.buzuriu.dogapp.viewModels.MyMeetingDetailViewModel
import com.buzuriu.dogapp.views.AddMeetingActivity
import com.buzuriu.dogapp.views.MeetingDetailActivity
import com.buzuriu.dogapp.views.MyMeetingDetailActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@SuppressLint("NotifyDataSetChanged")
class MyMeetingsViewModel : BaseViewModel() {
    var meetingAdapter: MyMeetingAdapter?

    private var meetingsList = ArrayList<IMeetingObj>()

    private var pastMeetingsICreated = ArrayList<MyCustomMeetingObj>()
    private var meetingsICreated = ArrayList<MyCustomMeetingObj>()
    private var meetingsIJoin = ArrayList<MyCustomMeetingObj>()
    private var meetingsICreatedText = "Created by me"
    private var meetingsIJoinText = "Joined by me"


    init {
        getAllMeetingsThatUserCreated()
        getAllMeetingsThatUserJoined()

        if (meetingsICreated.size > 0) {
            meetingsList.add(MeetingSectionObj(meetingsICreatedText))
            meetingsList.addAll(meetingsICreated)
        }
        else
        {
            meetingsList.add(MeetingSectionObj("You haven't created any meetings yet"))
        }

        if (meetingsIJoin.size > 0) {
            meetingsList.add(MeetingSectionObj(meetingsIJoinText))
            meetingsList.addAll(meetingsIJoin)
        }
        else
        {
            meetingsList.add(MeetingSectionObj("You haven't joined any meetings yet"))
        }

        meetingAdapter = MyMeetingAdapter(meetingsList, ::selectedMeeting, this)
        meetingAdapter!!.notifyDataSetChanged()
    }

    override fun onResume() {
        val isRefreshNeeded = exchangeInfoService.get<Boolean>(this::class.qualifiedName!!)
        if (isRefreshNeeded != null && isRefreshNeeded == true) {
            // clear meetings list and add them again updated
            refreshList()
        }
    }

    fun refreshList() {
        meetingsList.clear()
        meetingsICreated.clear()
        meetingsIJoin.clear()

        getAllMeetingsThatUserCreated()
        getAllMeetingsThatUserJoined()

        if (meetingsICreated.size > 0) {
            meetingsList.add(MeetingSectionObj(meetingsICreatedText))
            meetingsList.addAll(meetingsICreated)
        }
        else
        {
            meetingsList.add(MeetingSectionObj("You haven't created any meetings yet"))
        }

        if (meetingsIJoin.size > 0) {
            meetingsList.add(MeetingSectionObj(meetingsIJoinText))
            meetingsList.addAll(meetingsIJoin)
        }
        else
        {
            meetingsList.add(MeetingSectionObj("You haven't joined any meetings yet"))
        }

        meetingAdapter!!.notifyDataSetChanged()
    }

    fun addMeeting() {
        if (!doesUserHaveAtLeastOneDog()) {
            snackMessageService.displaySnackBar("Please add your pet before participating to a meeting")
            return
        }

        viewModelScope.launch(Dispatchers.Main) {
            val hasPermission = requestPermissionKind(listOf(Manifest.permission.ACCESS_FINE_LOCATION)).await()
            if (!hasPermission) {
                snackMessageService.displaySnackBar("Location permission needed to add a meeting!")
                return@launch
            } else {
                navigationService.navigateToActivity(AddMeetingActivity::class.java, false)
            }
        }
    }

    private fun getAllMeetingsThatUserCreated() {
        val meetingsFromLocalDB =
            localDatabaseService.get<ArrayList<MyCustomMeetingObj>>(LocalDBItems.localMeetingsList)
        if (meetingsFromLocalDB != null) {
            // check if meeting is older than present
                for (meet in meetingsFromLocalDB)
                {
                    if (!MeetingUtils.isMeetingInThePast(meet.meetingObj!!)) {
                        meetingsICreated.add(meet)
                    }
                    else {
                        pastMeetingsICreated.add(meet)
                    }
                }
        }
    }

    private fun getAllMeetingsThatUserJoined()  {
        val meetingsFromLocalDB =
            localDatabaseService.get<ArrayList<MyCustomMeetingObj>>(LocalDBItems.meetingsUserJoined)
        if (meetingsFromLocalDB != null) {
            meetingsIJoin.addAll(meetingsFromLocalDB)
        }
    }

    private fun selectedMeeting(meeting: MyCustomMeetingObj) {
        if (isMeetingCreatedByMe(meeting)) {
            exchangeInfoService.put(MyMeetingDetailViewModel::class.java.name, meeting)
            navigationService.navigateToActivity(MyMeetingDetailActivity::class.java, false)
        } else {
            exchangeInfoService.put(MeetingDetailViewModel::class.java.name, meeting)
            navigationService.navigateToActivity(MeetingDetailActivity::class.java, false)
        }
    }

    fun removeMeetFromUserJoinedMeetings(meeting: MyCustomMeetingObj) {
        val allMeetingsThatUserJoinedList =
            localDatabaseService.get<ArrayList<MyCustomMeetingObj>>(LocalDBItems.meetingsUserJoined)
        val toBeRemoved =
            allMeetingsThatUserJoinedList!!.find { it.meetingObj!!.uid == meeting.meetingObj!!.uid }
        allMeetingsThatUserJoinedList.remove(toBeRemoved)
        localDatabaseService.add(LocalDBItems.meetingsUserJoined, allMeetingsThatUserJoinedList)
    }

    fun leaveMeeting(meeting: MyCustomMeetingObj) {
        alertMessageService.displayAlertDialog(
            "Leave?",
            "Are you sure you don't want to join this meeting with ${meeting.user!!.name}?",
            "Yes",
            object :
                IClickListener {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun clicked() {
                    var participantUid: String
                    viewModelScope.launch()
                    {
                        participantUid = getUserParticipantUid(meeting)!!

                        if (participantUid != "") {
                            viewModelScope.launch(Dispatchers.IO)
                            {
                                databaseService.leaveMeeting(meeting.meetingObj!!.uid!!,
                                    participantUid,
                                    object : IOnCompleteListener {
                                        override fun onComplete(
                                            successful: Boolean,
                                            exception: Exception?
                                        ) {
                                            removeMeetFromUserJoinedMeetings(meeting)
                                            refreshList()
                                            snackMessageService.displaySnackBar("Success")
                                        }
                                    })
                            }
                        }
                    }
                }
            })
    }

    suspend fun getUserParticipantUid(meeting: MyCustomMeetingObj): String? {
        return databaseService.fetchUserParticipantUidForMeeting(
            meeting.meetingObj!!.uid!!,
            currentUser!!.uid
        )
    }

    private fun isMeetingCreatedByMe(meeting: MyCustomMeetingObj): Boolean {
        return meeting.meetingObj!!.userUid == currentUser!!.uid
    }

    private fun doesUserHaveAtLeastOneDog(): Boolean {
        if (localDatabaseService.get<ArrayList<DogObj>>(LocalDBItems.localDogsList)!!.size < 1)
            return false
        return true
    }
}