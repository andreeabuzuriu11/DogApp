package com.buzuriu.dogapp.models

data class UserObj(
    var uid: String? = null,
    var email: String? = null,
    var name: String? = null,
    var phone: String? = null,
    var gender: String? = null,
    var rating: Float? = null,
    var imageUrl: String = "",
    var request: RequestObj? = null
)
