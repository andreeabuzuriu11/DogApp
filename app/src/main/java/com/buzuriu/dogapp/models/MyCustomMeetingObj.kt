package com.buzuriu.dogapp.models

import com.buzuriu.dogapp.enums.MeetingStateEnum

class MyCustomMeetingObj {
    var meetingObj: MeetingObj? = null
    var user: UserInfo? = null
    var dog: DogObj? = null
    var meetingStateEnum = MeetingStateEnum.NOT_JOINED

    constructor(meetingObj: MeetingObj, userInfo: UserInfo, dogObj: DogObj) {
        this.meetingObj = meetingObj
        this.user = userInfo
        this.dog = dogObj
        this.meetingStateEnum = MeetingStateEnum.NOT_JOINED
    }

    constructor(
        meetingObj: MeetingObj,
        userInfo: UserInfo,
        dogObj: DogObj,
        meetingStateEnum: MeetingStateEnum
    ) {
        this.meetingObj = meetingObj
        this.user = userInfo
        this.dog = dogObj
        this.meetingStateEnum = meetingStateEnum
    }
}