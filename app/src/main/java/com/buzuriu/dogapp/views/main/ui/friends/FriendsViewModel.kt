package com.buzuriu.dogapp.views.main.ui.friends

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.adapters.FriendsAdapter
import com.buzuriu.dogapp.adapters.UserAdapter
import com.buzuriu.dogapp.models.UserObj
import com.buzuriu.dogapp.viewModels.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FriendsViewModel : BaseViewModel() {

    var friends: UserObj? = null
    var friendsList: ArrayList<UserObj> = ArrayList()
    var friendsAdapter: FriendsAdapter? = null

    var doesUserHaveAnyFriends = MutableLiveData(false)
    var doesUserHaveAnyRequests = MutableLiveData(false)

    var isFriendTabSelected = MutableLiveData(true)
    init {

        friendsList.add(UserObj("mail@mail.com","Tommy","1234567","Male"))
        friendsList.add(UserObj("mail@mail.com","Jerry","1234567","Male"))
        doesUserHaveAnyFriends.value = friendsList.isNotEmpty()
        friendsAdapter = FriendsAdapter(friendsList)
    }

    fun searchFriends() {

    }

}