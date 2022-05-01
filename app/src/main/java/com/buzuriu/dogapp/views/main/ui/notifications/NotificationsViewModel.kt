package com.buzuriu.dogapp.views.main.ui.notifications

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.adapters.ReviewNotificationAdapter
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import com.buzuriu.dogapp.models.ReviewObj
import com.buzuriu.dogapp.utils.StringUtils
import com.buzuriu.dogapp.viewModels.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception

@SuppressLint("NotifyDataSetChanged")
class NotificationsViewModel : BaseViewModel() {

    var reviewNotificationAdapter: ReviewNotificationAdapter?
    private var pastMeetingsList = ArrayList<MyCustomMeetingObj>()
    var nrOfStars = MutableLiveData(0)

    init {
        pastMeetingsList =
            localDatabaseService.get<ArrayList<MyCustomMeetingObj>>("pastMeetingsUserJoined")!!

        reviewNotificationAdapter = ReviewNotificationAdapter(pastMeetingsList, this)
        reviewNotificationAdapter!!.notifyDataSetChanged()
    }

    fun saveReview(meeting: MyCustomMeetingObj) {
        val reviewUid = StringUtils.getRandomUID()
        Log.d("andreea333","${nrOfStars.value}")
       /* val reviewObj = ReviewObj(currentUser!!.uid, nrOfStars.value!!.toInt())
        viewModelScope.launch(Dispatchers.IO) {
            databaseService.storeReviewToUser(
                meeting.meetingObj!!.userUid!!,
                reviewUid,
                reviewObj,
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
        Log.d("andreea7", "${nrOfStars.value}")*/
    }
}
