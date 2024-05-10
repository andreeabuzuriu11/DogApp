package com.buzuriu.dogapp.models

import com.buzuriu.dogapp.enums.FriendshipStateEnum

data class UserObj(
    var uid: String? = null,
    var email: String? = null,
    var name: String? = null,
    var phone: String? = null,
    var gender: String? = null,
    var city: String = "",
    var rating: Float? = null,
    var imageUrl: String = "",
    var relationWithCurrentUser : FriendshipStateEnum = FriendshipStateEnum.NOT_REQUESTED
)
