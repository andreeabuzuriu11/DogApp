package com.buzuriu.dogapp.models

class ReviewObj {
    var uid : String? = null
    var userIdThatLeftReview: String? = null
    var userThatReviewIsFor : String? = null
    var numberOfStars : Float? = null
    var reviewText : String? = null

    constructor()

    constructor(uid: String, userIdThatLeftReview: String, userThatReviewIsFor: String, numberOfStars: Float, reviewText: String?) {
        this.uid = uid
        this.userIdThatLeftReview = userIdThatLeftReview
        this.userThatReviewIsFor = userThatReviewIsFor
        this.numberOfStars = numberOfStars
        this.reviewText = reviewText
    }
}