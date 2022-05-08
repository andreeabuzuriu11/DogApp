package com.buzuriu.dogapp.views.main.ui.notifications

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.adapters.ReviewNotificationAdapter
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import com.buzuriu.dogapp.viewModels.BaseViewModel
import com.buzuriu.dogapp.viewModels.PastMeetingDetailViewModel
import com.buzuriu.dogapp.viewModels.ReviewParticipantsViewModel
import com.buzuriu.dogapp.views.PastMeetingDetailActivity
import com.buzuriu.dogapp.views.ReviewParticipantsFragment
import com.buzuriu.dogapp.views.main.ui.OverlayActivity

@SuppressLint("NotifyDataSetChanged")
class NotificationsViewModel : BaseViewModel() {

    var reviewNotificationAdapter: ReviewNotificationAdapter?
    private var pastMeetingsList = ArrayList<MyCustomMeetingObj>()
    var nrOfStars = MutableLiveData(0)

    init {
        val pastMeetingsListFromLocalDB =
            localDatabaseService.get<ArrayList<MyCustomMeetingObj>>("pastMeetingsUserJoined")

        if (pastMeetingsListFromLocalDB != null)
        {
            pastMeetingsList.addAll(pastMeetingsListFromLocalDB)
        }

        reviewNotificationAdapter = ReviewNotificationAdapter(pastMeetingsList, ::selectedPastMeeting, this)
        reviewNotificationAdapter!!.notifyDataSetChanged()
    }

    private fun selectedPastMeeting(myCustomMeetingObj: MyCustomMeetingObj)
    {
        dataExchangeService.put(PastMeetingDetailViewModel::class.java.name, myCustomMeetingObj)
        navigationService.navigateToActivity(PastMeetingDetailActivity::class.java, false)
    }

    fun openReviewParticipantsFragment(myCustomMeetingObj: MyCustomMeetingObj)
    {
        dataExchangeService.put(ReviewParticipantsViewModel::class.java.name, myCustomMeetingObj)
        navigationService.showOverlay(
            OverlayActivity::class.java,
            false,
            OverlayActivity.fragmentClassNameParam,
            ReviewParticipantsFragment::class.qualifiedName
        )
    }
}
