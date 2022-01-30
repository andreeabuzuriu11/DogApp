package com.buzuriu.dogapp.listeners

import com.buzuriu.dogapp.models.MeetingObj

interface IGetMeetingListListener {
    fun getMeetingList(meetingList : ArrayList<MeetingObj>)
}