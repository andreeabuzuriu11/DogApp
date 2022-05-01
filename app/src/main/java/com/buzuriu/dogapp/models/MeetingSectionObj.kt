package com.buzuriu.dogapp.models

class MeetingSectionObj : IMeetingObj {
    var sectionName : String? = null

    constructor(sectionName: String?) {
        this.sectionName = sectionName
    }
}