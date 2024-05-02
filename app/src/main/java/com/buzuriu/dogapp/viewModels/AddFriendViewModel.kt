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
        var userSendingRequestUid: String = firebaseAuthService.getCurrentUser()!!.uid
        var userReceivingRequestUid: String = userReceivingRequest.uid!!


        viewModelScope.launch(Dispatchers.IO) {

            // todo read all reqs before

            var userThatSendsReq = databaseService.fetchRequestObj(userSendingRequestUid,object : IOnCompleteListener {
                override fun onComplete(successful: Boolean, exception: Exception?) {
                }
            })
            if (userThatSendsReq== null)
                userThatSendsReq = CreateNewReq()
            userThatSendsReq!!.ownRequests!!.add(userReceivingRequestUid)

            var userThatGetsReq = databaseService.fetchRequestObj(userReceivingRequestUid,object : IOnCompleteListener {
                override fun onComplete(successful: Boolean, exception: Exception?) {
                }
            })
            if (userThatGetsReq== null)
                userThatGetsReq = CreateNewReq()
            userThatGetsReq!!.friendsRequests!!.add(userSendingRequestUid)

            viewModelScope.launch {
                databaseService.newSendFriendRequest(currentUser!!.uid, userThatSendsReq,
                    object :
                        IOnCompleteListener {
                        override fun onComplete(successful: Boolean, exception: Exception?) {
                        }
                    })
            }

            databaseService.newSendFriendRequest(
                userReceivingRequestUid,
                userThatGetsReq,
                object :
                    IOnCompleteListener {
                    override fun onComplete(successful: Boolean, exception: Exception?) {
                    }
                })

        }
    }

    fun CreateNewReq(): RequestObj {
        return RequestObj(arrayListOf(), arrayListOf(), arrayListOf())
    }


}