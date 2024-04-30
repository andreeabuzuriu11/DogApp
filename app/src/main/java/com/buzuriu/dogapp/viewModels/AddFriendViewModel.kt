package com.buzuriu.dogapp.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.adapters.FriendsAdapter
import com.buzuriu.dogapp.adapters.UserAdapter
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.RequestObj
import com.buzuriu.dogapp.models.UserObj
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddFriendViewModel : BaseViewModel() {

    var foundUser: UserObj? = null
    var foundUsersList: ArrayList<UserObj> = ArrayList()
    var userAdapter: UserAdapter? = null


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
        val userSendingRequestUid: String = firebaseAuthService.getCurrentUser()!!.uid
        val userReceivingRequestUid: String = userReceivingRequest.uid!!

        // todo fix this duplicate code
        viewModelScope.launch {
            var currentUser = databaseService.fetchUserByUid(userSendingRequestUid)
            var userThatReceive = databaseService.fetchUserByUid(userReceivingRequestUid)

            var userThatSendsReq = currentUser!!.request
            var userThatReceiveReq = userThatReceive!!.request

            if (userThatSendsReq == null)
                userThatSendsReq = CreateNewReq()

            if (userThatReceiveReq == null)
                userThatReceiveReq = CreateNewReq()

            // add new info about the request
            if (!userThatSendsReq.ownRequests!!.contains(userReceivingRequestUid))
                userThatSendsReq.ownRequests!!.add(userReceivingRequestUid)

            if (!userThatReceiveReq.friendsRequests!!.contains(userSendingRequestUid))
                userThatReceiveReq.friendsRequests!!.add(userSendingRequestUid)

            viewModelScope.launch(Dispatchers.IO) {

                databaseService.updateRequestForUser(
                    userSendingRequestUid,
                    userThatSendsReq,
                    object :
                        IOnCompleteListener {
                        override fun onComplete(successful: Boolean, exception: Exception?) {
                        }
                    })
            }

            viewModelScope.launch(Dispatchers.IO) {
                databaseService.updateRequestForUser(
                    userReceivingRequestUid,
                    userThatReceiveReq,
                    object :
                        IOnCompleteListener {
                        override fun onComplete(successful: Boolean, exception: Exception?) {
                        }
                    })
            }

        }


    }

    fun CreateNewReq(): RequestObj {
        return RequestObj(arrayListOf(), arrayListOf(), arrayListOf())
    }

}