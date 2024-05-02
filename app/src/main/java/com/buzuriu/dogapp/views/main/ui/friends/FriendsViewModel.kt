package com.buzuriu.dogapp.views.main.ui.friends

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.adapters.FriendRequestAdapter
import com.buzuriu.dogapp.adapters.FriendsAdapter
import com.buzuriu.dogapp.adapters.UserAdapter
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.RequestObj
import com.buzuriu.dogapp.models.UserObj
import com.buzuriu.dogapp.services.DatabaseService
import com.buzuriu.dogapp.viewModels.BaseViewModel
import com.buzuriu.dogapp.views.AddFriendActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FriendsViewModel : BaseViewModel() {

    var foundUser: UserObj? = null
    var foundUsersList: ArrayList<UserObj> = ArrayList()
    var userAdapter: UserAdapter? = null

    var isFriendTabSelected = MutableLiveData(true)
    var doesUserHaveAnyFriends = MutableLiveData(false)
    var doesUserHaveAnyRequests = MutableLiveData(false)

    var friendsList: ArrayList<UserObj> = ArrayList()
    var friendsRequestList: ArrayList<UserObj> = ArrayList()
    var friendsAdapter: FriendsAdapter? = null
    var friendsRequestAdapter: FriendRequestAdapter? = null


    var currentUserReqObj: RequestObj? = null

    private val friendRequests = "FriendRequests"


    init {
        userAdapter = UserAdapter(foundUsersList, ::sendFriendRequest)

//        friendsAdapter = FriendsAdapter(friendsList, ::showFriendProfile)

        viewModelScope.launch {
            currentUserReqObj = databaseService.fetchRequestObj(currentUser!!.uid)
            if (currentUserReqObj != null) {
                if (currentUserReqObj!!.friendsRequests != null) {
                    var currentUserFriendsReq = currentUserReqObj!!.friendsRequests
                    if (currentUserFriendsReq != null) {
                        currentUserFriendsReq.forEach {
                            val user = databaseService.fetchUserByUid(it)
                            friendsRequestList.add(user!!)
                            doesUserHaveAnyRequests.value = friendsRequestList.isNotEmpty()

                        }
                    }
                }
                if (currentUserReqObj!!.myFriends != null) {
                    var currentUserMyFriends = currentUserReqObj!!.myFriends
                    if (currentUserMyFriends != null) {
                        currentUserMyFriends.forEach {
                            val user = databaseService.fetchUserByUid(it)
                            friendsList.add(user!!)
                            doesUserHaveAnyFriends.value = friendsList.isNotEmpty()

                        }
                    }
                }

            }

        }

        doesUserHaveAnyFriends.value = friendsList.isNotEmpty()
        friendsAdapter = FriendsAdapter(friendsList, ::showFriendProfile)
        friendsAdapter!!.notifyDataSetChanged()

        doesUserHaveAnyRequests.value = friendsRequestList.isNotEmpty()
        friendsRequestAdapter = FriendRequestAdapter(friendsRequestList, this)
        friendsRequestAdapter!!.notifyDataSetChanged()
    }

    private fun friendRequestPressed(userObj: UserObj) {
        println("Friends request cell pressed")
    }

    fun findUser(searchedUserText: String) {
//        todo fix loading
//        showLoading(true)
        viewModelScope.launch(Dispatchers.Main) {
            val allUsers = databaseService.fetchUsers()
            if (allUsers != null) {
                for (user in allUsers)
                    if (user.email!!.contains(searchedUserText))
                        foundUser = user
                    else
                        if (user.name != null && user.name!!.contains(searchedUserText))
                            foundUser = user
            }

            if (foundUser == null) {
                return@launch
            }

        }


        if (foundUser != null) {
            if (!isUserAlreadyFound(foundUser!!)) {
                foundUsersList.add(foundUser!!)
                //todo fix loading
//                showLoading(false)
            }

            if (!isUserMatchingWithCurrentSearch(foundUser!!, searchedUserText))
                foundUsersList.remove(foundUser)
        }

        if (isSearchedTextEmpty(searchedUserText)) {
            foundUsersList.removeAll(foundUsersList.toSet())
            //todo fix loading
//            showLoading(false)
        }

        userAdapter!!.notifyDataSetChanged()
    }


    private fun isUserAlreadyFound(userObj: UserObj): Boolean {
        if (foundUsersList.contains(userObj))
            return true
        return false
    }

    private fun isUserMatchingWithCurrentSearch(
        userObj: UserObj,
        searchedUserText: String
    ): Boolean {
        if (userObj.email!!.contains(searchedUserText))
            return true
        if (userObj.name!!.contains(searchedUserText))
            return true
        return false
    }

    private fun isSearchedTextEmpty(searchedUserText: String): Boolean {
        if (searchedUserText.length == 1 || searchedUserText == "")
            return true
        return false
    }

    fun showLoading(isVisible: Boolean) {
        showLoadingView(isVisible)
    }

    fun showFriendProfile(userObj: UserObj) {
        Log.d("click", "show friend prfile")
    }

    fun sendFriendRequest(userReceivingRequest: UserObj) {
        var userSendingRequestUid: String = firebaseAuthService.getCurrentUser()!!.uid

    }


    fun searchFriends() {
        navigationService.navigateToActivity(AddFriendActivity::class.java, false)
    }

    fun acceptRequest(userObj: UserObj) {
        viewModelScope.launch {
            databaseService.acceptRequest(currentUser!!.uid, userObj.uid!!)
        }
    }

    fun declineRequest(userObj: UserObj) {
        println("Decline req from" + userObj.name)
    }
}