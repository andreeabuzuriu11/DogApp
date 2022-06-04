package com.buzuriu.dogapp.models

import com.buzuriu.dogapp.enums.MeetingStateEnum

class MyCustomMeetingObj : IMeetingObj{
    var meetingObj: MeetingObj? = null
    var user: UserObj? = null
    var dog: DogObj? = null
    var meetingStateEnum = MeetingStateEnum.NOT_JOINED

    constructor(meetingObj: MeetingObj, userObj: UserObj, dogObj: DogObj) {
        this.meetingObj = meetingObj
        this.user = userObj
        this.dog = dogObj
        this.meetingStateEnum = MeetingStateEnum.NOT_JOINED
    }

    constructor(
        meetingObj: MeetingObj,
        userObj: UserObj,
        dogObj: DogObj,
        meetingStateEnum: MeetingStateEnum
    ) {
        this.meetingObj = meetingObj
        this.user = userObj
        this.dog = dogObj
        this.meetingStateEnum = meetingStateEnum
    }
}