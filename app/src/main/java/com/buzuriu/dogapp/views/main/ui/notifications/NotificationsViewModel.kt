package com.buzuriu.dogapp.views.main.ui.notifications

import android.annotation.SuppressLint
import com.buzuriu.dogapp.adapters.ReviewNotificationAdapter
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import com.buzuriu.dogapp.viewModels.BaseViewModel

@SuppressLint("NotifyDataSetChanged")
class NotificationsViewModel : BaseViewModel() {

    var reviewNotifAdapter: ReviewNotificationAdapter?

    private var pastMeetingsList = ArrayList<MyCustomMeetingObj>()


    init {
        pastMeetingsList =  localDatabaseService.get<ArrayList<MyCustomMeetingObj>>("pastMeetingsUserJoined")!!

        reviewNotifAdapter = ReviewNotificationAdapter(pastMeetingsList)
        reviewNotifAdapter!!.notifyDataSetChanged()
    }
}