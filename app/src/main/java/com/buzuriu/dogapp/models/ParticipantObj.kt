package com.buzuriu.dogapp.models

class ParticipantObj {
    var uid: String? = null
    var dogUid: String? = null
    var userUid: String? = null

    constructor()

    constructor(userUid: String, dogUid: String) {
        this.userUid = userUid
        this.dogUid = dogUid
    }

    constructor(uid: String, userUid: String, dogUid: String) {
        this.uid = uid
        this.userUid = userUid
        this.dogUid = dogUid
    }
}