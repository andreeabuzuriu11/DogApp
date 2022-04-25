package com.buzuriu.dogapp.models

class ParticipantObj {
    var dogUid: String? = null
    var userUid: String? = null

    constructor()

    constructor(userUid: String, dogUid: String) {
        this.userUid = userUid
        this.dogUid = dogUid
    }
}