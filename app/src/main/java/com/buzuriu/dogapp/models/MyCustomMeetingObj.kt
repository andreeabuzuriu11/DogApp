package com.buzuriu.dogapp.models

class MyCustomMeetingObj {
    var meetingObj: MeetingObj? = null
    var user: UserInfo? = null
    var dog: DogObj? = null

    constructor(meetingObj: MeetingObj, userInfo: UserInfo, dogObj: DogObj)
    {
        this.meetingObj = meetingObj
        this.user= userInfo
        this.dog = dogObj
    }
}