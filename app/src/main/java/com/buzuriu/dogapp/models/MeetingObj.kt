package com.buzuriu.dogapp.models;

import com.google.firebase.firestore.GeoPoint;

class MeetingObj {
    var uid: String? = null
    var date: Long? = null
    var location: GeoPoint? = null
    var dogUid : String? = null
    var userUid : String? = null

    constructor()

    constructor(uid: String, date:Long, location: GeoPoint, dogUid : String, userUid: String)
    {
        this.uid = uid
        this.date = date
        this.location = location
        this.dogUid = dogUid
        this.userUid = userUid
    }
}
