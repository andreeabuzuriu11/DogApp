package com.buzuriu.dogapp.views.main.ui.notifications

import android.util.Log
import com.buzuriu.dogapp.adapters.ReviewNotificationAdapter
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import com.buzuriu.dogapp.viewModels.BaseViewModel

class NotificationsViewModel : BaseViewModel() {

    var reviewNotifAdapter: ReviewNotificationAdapter?

    private var pastMeetingsList = ArrayList<MyCustomMeetingObj>()
    private var reviewNotifList: ArrayList<MyCustomMeetingObj> = ArrayList()


    init {
        pastMeetingsList =  localDatabaseService.get<ArrayList<MyCustomMeetingObj>>("pastMeetingsUserJoined")!!

        reviewNotifAdapter = ReviewNotificationAdapter(pastMeetingsList)
        reviewNotifAdapter!!.notifyDataSetChanged()

        printInfo()

    }

    private fun printInfo()
    {
        Log.d("andreea9", "${pastMeetingsList.size}")
        for (pastMeet in pastMeetingsList)
        {
            Log.d("andreea10", "${currentUser!!.email} has join ${pastMeet.user!!.name} and ${pastMeet.dog!!.name}")
        }
    }


}