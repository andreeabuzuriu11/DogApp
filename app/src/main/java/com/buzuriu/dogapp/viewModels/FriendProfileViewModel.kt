package com.buzuriu.dogapp.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.adapters.FriendMeetingAdapter
import com.buzuriu.dogapp.enums.MeetingStateEnum
import com.buzuriu.dogapp.listeners.IClickListener
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.*
import com.buzuriu.dogapp.utils.FieldsItems
import com.buzuriu.dogapp.utils.LocalDBItems
import com.buzuriu.dogapp.utils.LocalDataUtils
import com.buzuriu.dogapp.utils.MyCustomMeetingUtils
import com.buzuriu.dogapp.views.MeetingDetailActivity
import com.buzuriu.dogapp.views.SelectDogForJoinMeetFragment
import com.buzuriu.dogapp.views.main.ui.OverlayActivity
import com.buzuriu.dogapp.views.main.ui.my_dogs.MyDogsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class FriendProfileViewModel : BaseViewModel() {

    var user = MutableLiveData<UserObj>()

    var isPlaceholderVisible = MutableLiveData(false)
    private var meetingsList = ArrayList<MyCustomMeetingObj>()
    private var breedsList = ArrayList<String>()
    private val userJoinedMeetings = ArrayList<MyCustomMeetingObj>()
    var mapOfMeetingUidAndCurrentUserAsParticipant: MutableMap<String, ParticipantObj> =
        mutableMapOf()

    var meetingsAdapter: FriendMeetingAdapter? = null

    init {
        user.value = exchangeInfoService.get<UserObj>(this::class.qualifiedName!!)!!
        meetingsAdapter = FriendMeetingAdapter(meetingsList, ::selectedMeeting, this)

        viewModelScope.launch {
            fetchMeetingsForUser()
        }

    }

    private suspend fun fetchMeetingsForUser() {
        var dog: DogObj?
        showLoadingView(true)

        viewModelScope.launch(Dispatchers.IO) {
            val allMeetings: ArrayList<MeetingObj>? =
                databaseService.fetchUserMeetings(user.value!!.uid!!, object : IOnCompleteListener {
                    override fun onComplete(successful: Boolean, exception: Exception?) {
                    }
                })

            viewModelScope.launch(Dispatchers.Main) {
                if (allMeetings != null) {
                    for (meeting in allMeetings) {

                        dog = databaseService.fetchDogByUid(
                            meeting.dogUid!!,
                            object : IOnCompleteListener {
                                override fun onComplete(
                                    successful: Boolean,
                                    exception: Exception?
                                ) {
                                }
                            })

                        println("dog = " + dog!!.name)
                        val reviews = fetchUserReviews(meeting.userUid!!)
                        if (reviews != null) {
                            val meanOfReviews = getMeanOfReviews(reviews)
                            user.value!!.rating = meanOfReviews
                            val meetingObj = MyCustomMeetingObj(meeting, user.value!!, dog!!)
                            meetingsList.add(meetingObj)
                        } else {
                            val meetingObj = MyCustomMeetingObj(meeting, user.value!!, dog!!)
                            meetingsList.add(meetingObj)
                        }
                    }
                }
                meetingsAdapter!!.notifyDataSetChanged()

            }


        }

        showLoadingView(false)
    }


    private suspend fun fetchUserReviews(userUid: String): ArrayList<ReviewObj>? {
        return databaseService.fetchReviewsFor(FieldsItems.userThatReviewIsFor, userUid)
    }

    private fun getMeanOfReviews(reviews: ArrayList<ReviewObj>): Float {
        var sum = 0.0f
        for (review in reviews) {
            sum += review.numberOfStars!!
        }
        return sum / reviews.size
    }


    private fun selectedMeeting(myCustomMeetingObj: MyCustomMeetingObj) {
        exchangeInfoService.put(MeetingDetailViewModel::class.java.name, myCustomMeetingObj)
        navigationService.navigateToActivity(MeetingDetailActivity::class.java, false)
    }


    internal fun deleteFriend() {
        alertMessageService.displayAlertDialog(
            "Delete user?",
            "Are you sure you want to delete ${user.value!!.name} from your friends list?",
            "Yes, delete it",
            object :
                IClickListener {
                override fun clicked() {
                    //
                    deleteFriendFromDatabase()
                }
            })
    }

    fun deleteFriendFromDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            databaseService.deleteFriend(currentUser!!.uid, user.value!!.uid!!, object :
                IOnCompleteListener {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onComplete(successful: Boolean, exception: Exception?) {

                    snackMessageService.displaySnackBar("Your friend " + user.value!!.name + " was successfully deleted")
                    navigationService.closeCurrentActivity()
                }
            })
        }
    }

    // todo fix duplicated

    fun joinOrLeaveMeeting(meeting: MyCustomMeetingObj) {
        if (!LocalDataUtils.doesUserHaveAtLeastOneDog(localDatabaseService)) {
            snackMessageService.displaySnackBar("Please add your pet before participating to a meeting")
            return
        }

        when {
            meeting.meetingStateEnum == MeetingStateEnum.NOT_JOINED -> {
                exchangeInfoService.put(SelectDogForJoinMeetViewModel::class.java.name, meeting)

                navigationService.showOverlay(
                    OverlayActivity::class.java,
                    false,
                    LocalDBItems.fragmentName,
                    SelectDogForJoinMeetFragment::class.qualifiedName
                )

            }
            hasUserAlreadyJoinedMeeting(meeting) -> {
                return
            }
            meeting.meetingStateEnum == MeetingStateEnum.JOINED -> {
                alertMessageService.displayAlertDialog(
                    "Leave?",
                    "Are you sure you don't want to join this meeting with ${meeting.user!!.name}?",
                    "Yes",
                    object :
                        IClickListener {
                        override fun clicked() {
                            meeting.meetingStateEnum = MeetingStateEnum.NOT_JOINED
                            meetingsAdapter!!.notifyItemChanged(meetingsList.indexOf(meeting))
                            val participant =
                                mapOfMeetingUidAndCurrentUserAsParticipant[meeting.meetingObj!!.uid]
                            if (participant != null) {
                                viewModelScope.launch(Dispatchers.IO)
                                {
                                    databaseService.leaveMeeting(meeting.meetingObj!!.uid!!,
                                        participant.uid!!,
                                        object : IOnCompleteListener {
                                            override fun onComplete(
                                                successful: Boolean,
                                                exception: Exception?
                                            ) {
                                                MyCustomMeetingUtils.removeMeetFromUserJoinedMeetings(meeting, localDatabaseService)
                                                snackMessageService.displaySnackBar("Your intention of not attending this walk with ${meeting.user!!.name} successfully saved")
                                            }
                                        })
                                }
                            }
                        }
                    })
            }
        }
    }


    // todo fix duplicated

    private fun hasUserAlreadyJoinedMeeting(meeting: MyCustomMeetingObj): Boolean {
        var meetingsThatUserAlreadyJoined = ArrayList<MyCustomMeetingObj>()
        viewModelScope.launch {
            meetingsThatUserAlreadyJoined = getAllMeetingsThatUserJoined()
        }

        if (meetingsThatUserAlreadyJoined.find { it.meetingObj!!.uid == meeting.meetingObj!!.uid } != null) {
            return true
        }
        return false
    }


    // todo fix duplicated

    private suspend fun getAllMeetingsThatUserJoined(): ArrayList<MyCustomMeetingObj> {
        var allMeetingsParticipants: ArrayList<ParticipantObj>

        for (meeting in meetingsList) {
            allMeetingsParticipants =
                databaseService.fetchAllMeetingParticipants(meeting.meetingObj?.uid!!)!!
            for (meet in allMeetingsParticipants)
                if (meet.userUid == currentUser!!.uid) {
                    userJoinedMeetings.add(meeting)
                    MyCustomMeetingUtils.changeStateOfMeeting(meeting, userJoinedMeetings)
                    mapOfMeetingUidAndCurrentUserAsParticipant[meeting.meetingObj?.uid!!] =
                        meet
                }
        }
        return userJoinedMeetings
    }


}