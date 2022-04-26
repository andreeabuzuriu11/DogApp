package com.buzuriu.dogapp.models

import com.google.firebase.firestore.GeoPoint

class MeetingObj {
    var uid: String? = null
    var date: Long? = null
    var location: GeoPoint? = null
    var dogUid : String? = null
    var userUid : String? = null
    var dogGender: String? = null
    var dogBreed: String? = null
    var userGender: String? = null
    var participants: ArrayList<ParticipantObj>? = null

    constructor()

    constructor(uid: String, date:Long, location: GeoPoint, dogUid : String, userUid: String,
                dogGender: String, dogBreed: String, userGender: String)
    {
        this.uid = uid
        this.date = date
        this.location = location
        this.dogUid = dogUid
        this.userUid = userUid
        this.dogGender = dogGender
        this.dogBreed = dogBreed
        this.userGender = userGender
    }

    constructor(uid: String, date:Long, location: GeoPoint, dogUid : String, userUid: String,
                dogGender: String, dogBreed: String, userGender: String, participants: ArrayList<ParticipantObj>)
    {
        this.uid = uid
        this.date = date
        this.location = location
        this.dogUid = dogUid
        this.userUid = userUid
        this.dogGender = dogGender
        this.dogBreed = dogBreed
        this.userGender = userGender
        participants.addAll(participants)
    }
}
