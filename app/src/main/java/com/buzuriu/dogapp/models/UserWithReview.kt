package com.buzuriu.dogapp.models

class UserWithReview {
    var userUid : String? = null
    var userInfo : UserInfo? = null
    var reviewObj : ReviewObj? = null

    constructor(userUid: String, userInfo: UserInfo, reviewObj: ReviewObj) {
        this.userUid = userUid
        this.userInfo = userInfo
        this.reviewObj = reviewObj
    }
}