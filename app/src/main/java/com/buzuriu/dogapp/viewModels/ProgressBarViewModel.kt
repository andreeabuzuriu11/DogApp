package com.buzuriu.dogapp.viewModels

import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.*
import com.buzuriu.dogapp.utils.LocalDBItems
import com.buzuriu.dogapp.views.auth.LoginActivity
import com.buzuriu.dogapp.views.main.MainActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception

class ProgressBarViewModel : BaseViewModel() {

    init {
        if (Firebase.auth.currentUser != null) {
            viewModelScope.launch(Dispatchers.IO) {
                delay(1000)
                async {
                    prepareForMain()
                    getUserAccountInfo()
                    getAllMeetingsThatUserJoined()
                    navigationService.navigateToActivity(MainActivity::class.java, true)
                }
            }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                delay(1000)
                navigationService.navigateToActivity(LoginActivity::class.java, true)
            }
        }

    }

    private suspend fun prepareForMain() {
        val userDogs = databaseService.fetchUserDogs(currentUser!!.uid)
        if (userDogs != null) {
            localDatabaseService.add(LocalDBItems.localDogsList, userDogs)
        }

        var user: UserObj?
        var dog: DogObj?
        val allCustomMeetings = ArrayList<MyCustomMeetingObj>()

        val userMeetings: ArrayList<MeetingObj>? =
            databaseService.fetchUserMeetings(currentUser!!.uid, object : IOnCompleteListener{
                override fun onComplete(successful: Boolean, exception: Exception?) {

                }
            })

        if (userMeetings != null) {
            for (meeting in userMeetings) {
                user =
                    databaseService.fetchUserByUid(meeting.userUid!!, object : IOnCompleteListener {
                        override fun onComplete(successful: Boolean, exception: Exception?) {

                        }
                    })
                dog = databaseService.fetchDogByUid(meeting.dogUid!!)

                if (dog != null) {
                    if (user != null)
                    {
                        val meetingObj = MyCustomMeetingObj(meeting, user, dog)
                        allCustomMeetings.add(meetingObj)
                    }

                }
            }

            localDatabaseService.add(LocalDBItems.localMeetingsList, allCustomMeetings)
        }
    }

    private suspend fun getAllMeetingsThatUserJoined() {
        var allMeetingsParticipants: ArrayList<ParticipantObj>
        val allMeetingsThatUserJoined = ArrayList<MyCustomMeetingObj>()
        var user: UserObj?
        var dog: DogObj?
        val allOtherMeetings: ArrayList<MeetingObj> =
            databaseService.fetchAllOtherMeetings(currentUser!!.uid, object : IOnCompleteListener {
                override fun onComplete(successful: Boolean, exception: Exception?) {}
            })!!

        for (meeting in allOtherMeetings) {
            allMeetingsParticipants =
                databaseService.fetchAllMeetingParticipants(meeting.uid!!)!!
            for (participant in allMeetingsParticipants)
                if (participant.userUid == currentUser!!.uid) {
                    user = databaseService.fetchUserByUid(
                        meeting.userUid!!,
                        object : IOnCompleteListener {
                            override fun onComplete(successful: Boolean, exception: Exception?) {

                            }
                        })
                    dog = databaseService.fetchDogByUid(meeting.dogUid!!)

                    if (dog != null) {
                        val meetingObj = MyCustomMeetingObj(meeting, user!!, dog)
                        allMeetingsThatUserJoined.add(meetingObj)
                    }
                }
        }
        localDatabaseService.add(LocalDBItems.meetingsUserJoined, allMeetingsThatUserJoined)
    }

    private suspend fun getUserAccountInfo() {
        val userObj: UserObj? =
            databaseService.fetchUserByUid(currentUser!!.uid, object : IOnCompleteListener {
                override fun onComplete(successful: Boolean, exception: Exception?) {

                }
            })

        if (userObj != null) {
            localDatabaseService.add(LocalDBItems.currentUser, userObj)
        } else {
            navigationService.navigateToActivity(LoginActivity::class.java, true)
        }
    }

}
