package com.buzuriu.dogapp.views.main.ui.notifications

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.adapters.ReviewNotificationAdapter
import com.buzuriu.dogapp.models.IMeetingObj
import com.buzuriu.dogapp.models.MeetingSectionObj
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import com.buzuriu.dogapp.utils.LocalDBItems
import com.buzuriu.dogapp.viewModels.BaseViewModel
import com.buzuriu.dogapp.viewModels.MyMeetingDetailViewModel
import com.buzuriu.dogapp.viewModels.PastMeetingDetailViewModel
import com.buzuriu.dogapp.viewModels.ReviewParticipantsViewModel
import com.buzuriu.dogapp.views.MyMeetingDetailActivity
import com.buzuriu.dogapp.views.PastMeetingDetailActivity
import com.buzuriu.dogapp.views.ReviewParticipantsFragment
import com.buzuriu.dogapp.views.main.ui.OverlayActivity

@SuppressLint("NotifyDataSetChanged")
class NotificationsViewModel : BaseViewModel() {

    var reviewNotificationAdapter: ReviewNotificationAdapter?
    private var pastMeetingsUserJoinedList = ArrayList<MyCustomMeetingObj>()
    private var pastMeetingsUserCreatedList = ArrayList<MyCustomMeetingObj>()
    private var allPastMeetings = ArrayList<IMeetingObj>()
    private var meetingsICreatedText = "Created by me"
    private var meetingsIJoinText = "Joined by me"
    var nrOfStars = MutableLiveData(0)

    init {
        getAllPastMeetingsThatUserCreated()
        getAllPastMeetingsThatUserJoined()

        if (pastMeetingsUserCreatedList.size > 0) {
            allPastMeetings.add(MeetingSectionObj(meetingsICreatedText))
            allPastMeetings.addAll(pastMeetingsUserCreatedList)
        } else {
            allPastMeetings.add(MeetingSectionObj("Empty list of past created meetings"))
        }

        if (pastMeetingsUserJoinedList.size > 0) {
            allPastMeetings.add(MeetingSectionObj(meetingsIJoinText))
            allPastMeetings.addAll(pastMeetingsUserJoinedList)
        } else {
            allPastMeetings.add(MeetingSectionObj("Empty list of past joined meetings"))
        }

        reviewNotificationAdapter =
            ReviewNotificationAdapter(allPastMeetings, ::selectedPastMeeting, this)
        reviewNotificationAdapter!!.notifyDataSetChanged()
    }

    private fun getAllPastMeetingsThatUserCreated() {
        val pastMeetingsUserCreatedListFromLocalDB =
            localDatabaseService.get<ArrayList<MyCustomMeetingObj>>(LocalDBItems.pastMeetingsUserCreated)
        if (pastMeetingsUserCreatedListFromLocalDB != null) {
            pastMeetingsUserCreatedList.addAll(pastMeetingsUserCreatedListFromLocalDB)
        }
    }

    private fun getAllPastMeetingsThatUserJoined() {
        val pastMeetingsUserJoinedListFromLocalDB =
            localDatabaseService.get<ArrayList<MyCustomMeetingObj>>(LocalDBItems.pastMeetingsUserJoined)
        if (pastMeetingsUserJoinedListFromLocalDB != null) {
            pastMeetingsUserJoinedList.addAll(pastMeetingsUserJoinedListFromLocalDB)
        }

    }

    private fun selectedPastMeeting(myCustomMeetingObj: MyCustomMeetingObj) {
        if (isMeetingCreatedByMe(myCustomMeetingObj)) {
            exchangeInfoService.put(MyMeetingDetailViewModel::class.java.name, myCustomMeetingObj)
            navigationService.navigateToActivity(MyMeetingDetailActivity::class.java, false)
        } else {
            exchangeInfoService.put(PastMeetingDetailViewModel::class.java.name, myCustomMeetingObj)
            navigationService.navigateToActivity(PastMeetingDetailActivity::class.java, false)
        }
    }

    private fun isMeetingCreatedByMe(meeting: MyCustomMeetingObj): Boolean {
        return meeting.meetingObj!!.userUid == currentUser!!.uid
    }

    fun openReviewParticipantsFragment(myCustomMeetingObj: MyCustomMeetingObj) {
        exchangeInfoService.put(ReviewParticipantsViewModel::class.java.name, myCustomMeetingObj)
        navigationService.showOverlay(
            OverlayActivity::class.java,
            false,
            OverlayActivity.fragmentClassNameParam,
            ReviewParticipantsFragment::class.qualifiedName
        )
    }
}
