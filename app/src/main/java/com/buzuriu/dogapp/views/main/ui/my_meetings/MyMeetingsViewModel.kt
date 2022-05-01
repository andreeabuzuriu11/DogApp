package com.buzuriu.dogapp.views.main.ui.my_meetings

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.adapters.MyMeetingAdapter
import com.buzuriu.dogapp.listeners.IClickListener
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.models.IMeetingObj
import com.buzuriu.dogapp.models.MeetingSectionObj
import com.buzuriu.dogapp.models.MyCustomMeetingObj
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

    private var meetingsICreated = ArrayList<MyCustomMeetingObj>()
    private var meetingsIJoin = ArrayList<MyCustomMeetingObj>()
    private var meetingsICreatedText = "Created by me"
    private var meetingsIJoinText = "Joined by me"


    init {
        getAllMeetingsThatUserCreated()
        getAllMeetingsThatUserJoined()
        meetingsList.add(MeetingSectionObj(meetingsICreatedText))
        meetingsList.addAll(meetingsICreated)
        meetingsList.add(MeetingSectionObj(meetingsIJoinText))
        meetingsList.addAll(meetingsIJoin)

        meetingAdapter = MyMeetingAdapter(meetingsList, ::selectedMeeting, this)
        meetingAdapter!!.notifyDataSetChanged()
    }

    override fun onResume() {
        val isRefreshNeeded = dataExchangeService.get<Boolean>(this::class.qualifiedName!!)
        if (isRefreshNeeded != null && isRefreshNeeded == true) {
            // clear meetings list and add them again updated
            refreshList()
        }
    }

    fun refreshList() {
        meetingsList.clear()
        meetingsList.add(MeetingSectionObj(meetingsICreatedText))
        val myMeetingsFromLocalDB =
            localDatabaseService.get<ArrayList<MyCustomMeetingObj>>("localMeetingsList")

        if (myMeetingsFromLocalDB != null) {
            meetingsList.addAll(myMeetingsFromLocalDB)
        }

        meetingsList.add(MeetingSectionObj(meetingsIJoinText))
        val joinedMeetingsFromLocalDB =
            localDatabaseService.get<ArrayList<MyCustomMeetingObj>>("meetingsUserJoined")

        if (joinedMeetingsFromLocalDB != null) {
            meetingsList.addAll(joinedMeetingsFromLocalDB)
        }

        meetingAdapter!!.notifyDataSetChanged()
    }

    fun addMeeting() {
        if (!doesUserHaveAtLeastOneDog()) {
            dialogService.showSnackbar("Please add your pet before participating to a meeting")
            return
        }

        viewModelScope.launch(Dispatchers.Main) {
            val hasPermission = askLocationPermission().await()
            if (!hasPermission) {
                dialogService.showSnackbar("Location permission needed to add a meeting!")
                return@launch
            } else {
                navigationService.navigateToActivity(AddMeetingActivity::class.java, false)
            }
        }
    }

    private fun getAllMeetingsThatUserCreated() {
        val meetingsFromLocalDB =
            localDatabaseService.get<ArrayList<MyCustomMeetingObj>>("localMeetingsList")
        if (meetingsFromLocalDB != null) {
            meetingsICreated.addAll(meetingsFromLocalDB)
        }
    }

    private fun getAllMeetingsThatUserJoined() {
        val meetingsFromLocalDB =
            localDatabaseService.get<ArrayList<MyCustomMeetingObj>>("meetingsUserJoined")
        if (meetingsFromLocalDB != null) {
            meetingsIJoin.addAll(meetingsFromLocalDB)
        }
    }

    private fun selectedMeeting(meeting: MyCustomMeetingObj) {
        if (isMeetingCreatedByMe(meeting)) {
            dataExchangeService.put(MyMeetingDetailViewModel::class.java.name, meeting)
            navigationService.navigateToActivity(MyMeetingDetailActivity::class.java, false)
        } else {
            dataExchangeService.put(MeetingDetailViewModel::class.java.name, meeting)
            navigationService.navigateToActivity(MeetingDetailActivity::class.java, false)
        }
    }

    fun removeMeetFromUserJoinedMeetings(meeting: MyCustomMeetingObj) {
        val allMeetingsThatUserJoinedList =
            localDatabaseService.get<ArrayList<MyCustomMeetingObj>>("meetingsUserJoined")
        val toBeRemoved =
            allMeetingsThatUserJoinedList!!.find { it.meetingObj!!.uid == meeting.meetingObj!!.uid }
        allMeetingsThatUserJoinedList.remove(toBeRemoved)
        localDatabaseService.add("meetingsUserJoined", allMeetingsThatUserJoinedList)
    }

    fun leaveMeeting(meeting: MyCustomMeetingObj) {
        dialogService.showAlertDialog(
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
                                            dialogService.showSnackbar("Success")
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
        if (localDatabaseService.get<ArrayList<DogObj>>("localDogsList")!!.size < 1)
            return false
        return true
    }
}