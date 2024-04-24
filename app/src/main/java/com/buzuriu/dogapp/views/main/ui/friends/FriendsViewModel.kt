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

    var foundUser: UserObj? = null
    var foundUsersList: ArrayList<UserObj> = ArrayList()
    var userAdapter: UserAdapter? = null
    var isFriendTabSelected = MutableLiveData(true)
    var doesUserHaveAnyFriends = MutableLiveData(false)
    var doesUserHaveAnyRequests = MutableLiveData(false)
    var friendsAdapter: FriendsAdapter? = null

    init {
        userAdapter = UserAdapter(foundUsersList, ::sendFriendRequest)
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

    fun sendFriendRequest(userReceivingRequest: UserObj) {
        var userSendingRequestUid: String = firebaseAuthService.getCurrentUser()!!.uid

//        var userReceivingRequestUid: String = userReceivingRequest.uid


        //databaseService.sendFriendRequest(, )
    }


//    var friends: UserObj? = null
//    var friendsList: ArrayList<UserObj> = ArrayList()
//



//    init {
//
//        friendsList.add(UserObj("mail@mail.com", "Tommy", "1234567", "Male"))
//        friendsList.add(UserObj("mail@mail.com", "Jerry", "1234567", "Male"))
//        doesUserHaveAnyFriends.value = friendsList.isNotEmpty()
//        friendsAdapter = FriendsAdapter(friendsList)
//    }
//
//
    fun searchFriends() {

    }
}