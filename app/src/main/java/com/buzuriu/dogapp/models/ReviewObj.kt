package com.buzuriu.dogapp.models

class ReviewObj {
    var userIdThatLeftReview: String? = null
    var numberOfStars : Float? = null

    constructor()

    constructor(userIdThatLeftReview: String, numberOfStars: Float) {
        this.userIdThatLeftReview = userIdThatLeftReview
        this.numberOfStars = numberOfStars
    }
}