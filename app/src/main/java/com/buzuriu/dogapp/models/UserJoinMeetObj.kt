package com.buzuriu.dogapp.models

class UserJoinMeetObj {
    var meetingUid: String? = null
    var dogUid: String? = null

    constructor(meetingUid: String, dogUid: String)
    {
        this.meetingUid =  meetingUid
        this.dogUid =  dogUid
    }
}