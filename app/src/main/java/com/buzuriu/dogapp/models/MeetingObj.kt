package com.buzuriu.dogapp.models;

import com.google.firebase.firestore.GeoPoint;

class MeetingObj {
    var date: Long? = null
    var location: GeoPoint? = null
    var dogUid : String? = null
    var uid: String? = null

    constructor()

    constructor(date:Long, location: GeoPoint, dogUid : String)
    {
        this.date = date
        this.location = location
        this.dogUid = dogUid
    }
}
