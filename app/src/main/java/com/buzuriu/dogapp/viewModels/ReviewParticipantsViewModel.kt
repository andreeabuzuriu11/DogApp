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
            fetchAllParticipantsForMeeting()
        }
    }


    fun saveReviewInDatabase(userWithReview: UserWithReview) {
        Log.d(
            "andreea",
            "review for ${userWithReview.userInfo} is ${userWithReview.reviewObj!!.numberOfStars}"
        )

        val review = didCurrentUserAlreadyReviewUser(userWithReview.userUid!!)
        if (review != null) {
            viewModelScope.launch(Dispatchers.IO) {
                databaseService.updateReview(
                    review.uid!!,
                    userWithReview.reviewObj!!.numberOfStars!!,
                    object : IOnCompleteListener {
                        override fun onComplete(successful: Boolean, exception: Exception?) {
                            if (successful) {
                                viewModelScope.launch(Dispatchers.Main) {
                                    dialogService.showSnackbar("Review edited successfully")
                                    //TODO also change in localDatabase


                                    delay(2000)

                                }
                            } else {
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
            return
        }

        val reviewUid = StringUtils.getRandomUID()
        userWithReview.reviewObj!!.userIdThatLeftReview = currentUser!!.uid
        userWithReview.reviewObj!!.userThatReviewIsFor = userWithReview.userUid
        userWithReview.reviewObj!!.uid = reviewUid

        viewModelScope.launch(Dispatchers.IO) {
            databaseService.storeReview(
                reviewUid,
                userWithReview.reviewObj!!,
                object : IOnCompleteListener {
                    override fun onComplete(successful: Boolean, exception: Exception?) {
                        if (successful) {
                            viewModelScope.launch(Dispatchers.Main) {
                                dialogService.showSnackbar("Review added successfully")
                                delay(2000)
                            }
                        } else {
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

    private fun didCurrentUserAlreadyReviewUser(userUid: String): ReviewObj? {
        var review: ReviewObj? = null
        var listOfReviews =
            localDatabaseService.get<java.util.ArrayList<ReviewObj>>("reviewsUserLeft")
        if (listOfReviews != null) {
            review =
                listOfReviews.find { it.userIdThatLeftReview == currentUser!!.uid && it.userThatReviewIsFor == userUid }
        }
        return review
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
            Log.d("andreeaaaaaaaa", "${list.size}")
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