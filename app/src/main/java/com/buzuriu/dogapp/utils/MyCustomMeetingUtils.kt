package com.buzuriu.dogapp.utils

import com.buzuriu.dogapp.enums.MeetingStateEnum
import com.buzuriu.dogapp.models.MyCustomMeetingObj

class MyCustomMeetingUtils {
    companion object {
        private fun changeStateAccordingly(
            meeting: MyCustomMeetingObj,
            meetingStateEnum: MeetingStateEnum
        ) {
            meeting.meetingStateEnum = meetingStateEnum
        }


        fun changeStateOfMeeting(
            meeting: MyCustomMeetingObj,
            userJoinedMeetings: ArrayList<MyCustomMeetingObj>
        ) {
            if (hasUserJoinedThisMeeting(meeting, userJoinedMeetings)) {
                changeStateAccordingly(meeting, MeetingStateEnum.JOINED)
            } else {
                changeStateAccordingly(meeting, MeetingStateEnum.NOT_JOINED)
            }
        }

        fun hasUserJoinedThisMeeting(
            meeting: MyCustomMeetingObj,
            userJoinedMeetings: ArrayList<MyCustomMeetingObj>
        ): Boolean {
            for (userJoinMeeting in userJoinedMeetings)
                if (meeting.meetingObj!!.uid == userJoinMeeting.meetingObj!!.uid)
                    return true
            return false
        }
    }


}