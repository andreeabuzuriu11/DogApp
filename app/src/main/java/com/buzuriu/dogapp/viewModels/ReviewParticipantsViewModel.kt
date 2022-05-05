package com.buzuriu.dogapp.viewModels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.adapters.RatingUserCellAdapter
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.*
import com.buzuriu.dogapp.utils.StringUtils
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ReviewParticipantsViewModel : BaseViewModel() {

    private var reviewList = ArrayList<UserWithReview>()
    var ratingUserCellAdapter: RatingUserCellAdapter? = null


    var pastMeeting = MutableLiveData<MyCustomMeetingObj>()
    var myLatLng = MutableLiveData<LatLng>()

    init {
        pastMeeting.value = dataExchangeService.get<MyCustomMeetingObj>(this::class.java.name)
        ratingUserCellAdapter = RatingUserCellAdapter(reviewList, this)


        // reviewNotificationAdapter = ReviewNotificationAdapter(pastMeetingsList, ::selectedPastMeeting, this)
        viewModelScope.launch {
            reviewList = fetchAllParticipantsForMeeting()
        }

        Log.d("andreea9999", "${reviewList.size}")

        viewModelScope.launch {
            fetchAllReviewsForParticipantsToThisMeeting()
        }
    }



    fun saveReviewInDatabase(userWithReview: UserWithReview)
    {
        Log.d("andreea", "review for ${userWithReview.userInfo} is ${userWithReview.reviewObj!!.numberOfStars}")

        val reviewUid = StringUtils.getRandomUID()
        userWithReview.reviewObj!!.userIdThatLeftReview = currentUser!!.uid
        userWithReview.reviewObj!!.uid = reviewUid

         viewModelScope.launch(Dispatchers.IO) {
             databaseService.storeReviewToUser(
                 userWithReview.userUid!!,
                 reviewUid,
                 userWithReview.reviewObj!!,
                 object : IOnCompleteListener {
                     override fun onComplete(successful: Boolean, exception: Exception?) {
                         if (successful) {
                             viewModelScope.launch(Dispatchers.Main) {
                                 dialogService.showSnackbar("Review added successfully")
                                 delay(2000)
                             }
                         }
                         else
                         {
                             viewModelScope.launch(Dispatchers.Main) {
                                 if (!exception?.message.isNullOrEmpty())
                                     dialogService.showSnackbar(exception!!.message!!)
                                 else dialogService.showSnackbar(R.string.unknown_error)
                                 delay(2000)
                             }
                         }
                     }
                 })
         }
    }

    fun close() {
        navigationService.closeCurrentActivity()
    }

    @SuppressLint("NotifyDataSetChanged")
    private suspend fun fetchAllParticipantsForMeeting() : ArrayList<UserWithReview>{
        Log.d("andreea7", "pe aicisa")
        ShowLoadingView(true)
        viewModelScope.launch(Dispatchers.IO) {
            val list = fetchAllParticipantsForMeetingFromDatabase()
            ShowLoadingView(false)
            viewModelScope.launch(Dispatchers.Main) {
                reviewList.clear()
                reviewList.addAll(list)
                for (item in list)
                {
                    Log.d("andreea4", "${item.userInfo!!.name} has ${item.reviewObj}")
                }
                ratingUserCellAdapter!!.notifyDataSetChanged()
            }
        }
        return reviewList
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun fetchAllReviewsForParticipantsToThisMeeting() {
        var numberOfStars : Float? = null
        Log.d("andreea8", "pe aicisa")
        Log.d("andreea88888", "${reviewList.size}")
                for (userReview in reviewList) {
                    // numberOfStars = databaseService.fetch
                    Log.d("andreea8", "user name = ${userReview.userInfo!!.name}")
                    Log.d("andreea8", "user id = ${userReview.userUid}")
                    Log.d("andreea8", "user id that left review = ${userReview.reviewObj!!.userIdThatLeftReview}")
                    Log.d("andreea8", "number of stars = ${userReview.reviewObj!!.numberOfStars}")
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
                        var userWithReview = UserWithReview(participant.userUid!!, user, ReviewObj())
                        userWithReviewList.add(userWithReview)
                    }
                }
            }
        }

        return userWithReviewList
    }
}