package com.buzuriu.dogapp.viewModels

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.adapters.RatingUserCellAdapter
import com.buzuriu.dogapp.models.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReviewParticipantsViewModel : BaseViewModel() {

    private var participantsList = ArrayList<ParticipantObj>()
    private var reviewList = ArrayList<UserWithReview>()
    var ratingUserCellAdapter: RatingUserCellAdapter? = null
    var participantsUserName = ArrayList<String>()

    //var reviewNotificationAdapter: ReviewNotificationAdapter?

    var pastMeeting = MutableLiveData<MyCustomMeetingObj>()
    var myLatLng = MutableLiveData<LatLng>()

    init {
        pastMeeting.value = dataExchangeService.get<MyCustomMeetingObj>(this::class.java.name)
        ratingUserCellAdapter = RatingUserCellAdapter(reviewList, ::selectedCell)
        // reviewNotificationAdapter = ReviewNotificationAdapter(pastMeetingsList, ::selectedPastMeeting, this)

        viewModelScope.launch {
            fetchAllParticipantsForMeeting()
        }
    }

    private fun selectedCell(reviewObj: ReviewObj) {

    }

    fun close() {
        navigationService.closeCurrentActivity()
    }

    @SuppressLint("NotifyDataSetChanged")
    private suspend fun fetchAllParticipantsForMeeting() {
        ShowLoadingView(true)
        viewModelScope.launch(Dispatchers.IO) {
            val list = fetchAllParticipantsForMeetingFromDatabase()
            ShowLoadingView(false)
            viewModelScope.launch(Dispatchers.Main) {
                reviewList.clear()
                reviewList.addAll(list)
                ratingUserCellAdapter!!.notifyDataSetChanged()
            }
        }
    }

    private suspend fun fetchAllParticipantsForMeetingFromDatabase(): ArrayList<UserWithReview> {
        var user: UserInfo?
        var dog: DogObj?
        val allParticipantsNameList = ArrayList<ParticipantObj>()
        val userWithReviewList = ArrayList<UserWithReview>()

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
                        var userWithReview = UserWithReview(participant.userUid!!, ReviewObj())
                        userWithReviewList.add(userWithReview)
                    }
                }
            }
        }

        //return allParticipantsNameList
        return userWithReviewList
    }
}