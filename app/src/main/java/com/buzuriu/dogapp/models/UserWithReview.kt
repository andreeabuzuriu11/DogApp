package com.buzuriu.dogapp.models

class UserWithReview {
    var userUid : String? = null
    var userObj : UserObj? = null
    var reviewObj : ReviewObj? = null

    constructor(userUid: String, userObj: UserObj, reviewObj: ReviewObj) {
        this.userUid = userUid
        this.userObj = userObj
        this.reviewObj = reviewObj
    }
}