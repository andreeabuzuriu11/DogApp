package com.buzuriu.dogapp.views.main.ui.my_meetings

import com.buzuriu.dogapp.adapters.MyMeetingAdapter
import com.buzuriu.dogapp.models.MeetingObj
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import com.buzuriu.dogapp.viewModels.BaseViewModel

class MyMeetingsViewModel : BaseViewModel() {
    var meetingsList = ArrayList<MyCustomMeetingObj>()
    var meetingAdapter: MyMeetingAdapter?

    init {
        val meetingsFromLocalDB = localDatabaseService.get<ArrayList<MyCustomMeetingObj>>("localMeetingsList")
        if (meetingsFromLocalDB != null) {
            meetingsList.addAll(meetingsFromLocalDB)
        }

        meetingAdapter = MyMeetingAdapter(meetingsList, ::selectedMeeting)
        meetingAdapter!!.notifyDataSetChanged()
    }

    private fun selectedMeeting(meeting: MyCustomMeetingObj)
    {

    }

}