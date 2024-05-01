package com.buzuriu.dogapp.models

class RequestObj {
    var ownRequests: ArrayList<String>? = null
    var friendsRequests: ArrayList<String>? = null
    var myFriends: ArrayList<String>? = null

    constructor(
        ownRequests: ArrayList<String>,
        friendsRequests: ArrayList<String>,
        myFriends: ArrayList<String>
    ) {
        this.ownRequests = ownRequests
        this.friendsRequests = friendsRequests
        this.myFriends = myFriends
    }

    constructor()
}
