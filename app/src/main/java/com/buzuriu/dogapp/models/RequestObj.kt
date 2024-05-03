package com.buzuriu.dogapp.models

import com.buzuriu.dogapp.enums.FriendshipStateEnum
import com.buzuriu.dogapp.enums.MeetingStateEnum

class RequestObj {
    var ownRequests: ArrayList<String>? = null
    var friendsRequests: ArrayList<String>? = null
    var myFriends: ArrayList<String>? = null
    var friendshipStateEnum = FriendshipStateEnum.NOT_REQUESTED


    constructor(
        ownRequests: ArrayList<String>,
        friendsRequests: ArrayList<String>,
        myFriends: ArrayList<String>,
        friendshipStateEnum: FriendshipStateEnum
    ) {
        this.ownRequests = ownRequests
        this.friendsRequests = friendsRequests
        this.myFriends = myFriends
        this.friendshipStateEnum = friendshipStateEnum
    }

    constructor()
}
