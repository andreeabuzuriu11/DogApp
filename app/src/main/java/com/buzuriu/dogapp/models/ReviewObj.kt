package com.buzuriu.dogapp.models

class ReviewObj {
    var uid : String? = null
    var userIdThatLeftReview: String? = null
    var userThatReviewIsFor : String? = null
    var numberOfStars : Float? = null

    constructor()

    constructor(uid: String, userIdThatLeftReview: String, userThatReviewIsFor: String, numberOfStars: Float) {
        this.uid = uid
        this.userIdThatLeftReview = userIdThatLeftReview
        this.userThatReviewIsFor = userThatReviewIsFor
        this.numberOfStars = numberOfStars
    }
}