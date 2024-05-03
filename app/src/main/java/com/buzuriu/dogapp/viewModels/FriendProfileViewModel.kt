package com.buzuriu.dogapp.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.adapters.FriendMeetingAdapter
import com.buzuriu.dogapp.listeners.IClickListener
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.*
import com.buzuriu.dogapp.utils.FieldsItems
import com.buzuriu.dogapp.utils.LocalDBItems
import com.buzuriu.dogapp.views.MeetingDetailActivity
import com.buzuriu.dogapp.views.main.ui.my_dogs.MyDogsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class FriendProfileViewModel : BaseViewModel() {

    var user = MutableLiveData<UserObj>()

    var isPlaceholderVisible = MutableLiveData(false)
    private var meetingsList = ArrayList<MyCustomMeetingObj>()

    var meetingsAdapter: FriendMeetingAdapter? = null

    init {
        user.value = exchangeInfoService.get<UserObj>(this::class.qualifiedName!!)!!
        meetingsAdapter = FriendMeetingAdapter(meetingsList, ::selectedMeeting, this)

// todo fix this logic
        viewModelScope.launch {
            fetchMeetingsForUser()
        }

    }
    private suspend fun fetchMeetingsForUser() {
        var dog: DogObj?
        val allCustomMeetings = ArrayList<MyCustomMeetingObj>()
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

                        dog = databaseService.fetchDogByUid(meeting.dogUid!!, object: IOnCompleteListener{
                            override fun onComplete(successful: Boolean, exception: Exception?) {
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


    internal fun deleteFriend()
    {
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
                    navigationService.closeCurrentActivity()

                }
            })
        }
    }
}