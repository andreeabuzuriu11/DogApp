package com.buzuriu.dogapp.models

class UserWithReview {
    var userInfo : String? = null
    var reviewObj : ReviewObj? = null

    constructor(userInfo: String, reviewObj: ReviewObj) {
        this.userInfo = userInfo
        this.reviewObj = reviewObj
    }
}