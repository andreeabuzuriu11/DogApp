package com.buzuriu.dogapp.models

class ReviewObj {
    var userIdThatLeftReview: String? = null
    var numberOfStars : Int? = null

    constructor()

    constructor(userIdThatLeftReview: String, numberOfStars: Int) {
        this.userIdThatLeftReview = userIdThatLeftReview
        this.numberOfStars = numberOfStars
    }
}