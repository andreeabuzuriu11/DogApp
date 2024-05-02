package com.buzuriu.dogapp.views.main.ui.friends

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.adapters.FriendRequestAdapter
import com.buzuriu.dogapp.adapters.FriendsAdapter
import com.buzuriu.dogapp.adapters.UserAdapter
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.RequestObj
import com.buzuriu.dogapp.models.UserObj
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

    private var friendsList: ArrayList<UserObj> = ArrayList()
    var friendsRequestList: ArrayList<UserObj> = ArrayList()
    var friendsAdapter: FriendsAdapter? = null
    var friendsRequestAdapter: FriendRequestAdapter? = null

    private var currentUserReqObj: RequestObj? = null


    init {
        friendsAdapter = FriendsAdapter(friendsList, ::showFriendProfile)
        friendsRequestAdapter = FriendRequestAdapter(friendsRequestList, this)

        viewModelScope.launch {
            fetchMyFriendAndFriendsRequest()
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private suspend fun fetchMyFriendAndFriendsRequest() {
        showLoadingView(true)
        viewModelScope.launch(Dispatchers.IO) {
            currentUserReqObj = databaseService.fetchRequestObj(currentUser!!.uid,object :
                IOnCompleteListener {
                override fun onComplete(successful: Boolean, exception: Exception?) {
                }
            })

            viewModelScope.launch(Dispatchers.Main) {
                if (currentUserReqObj != null) {
                    if (currentUserReqObj!!.friendsRequests != null) {
                        currentUserReqObj!!.friendsRequests?.forEach {
                            val user = databaseService.fetchUserByUid(it, object : IOnCompleteListener{
                                override fun onComplete(successful: Boolean, exception: java.lang.Exception?) {
                                    showLoadingView(false)
                                }
                            })
                            friendsRequestList.add(user!!)
                            doesUserHaveAnyRequests.value = friendsRequestList.isNotEmpty()

                        }
                    }
                    if (currentUserReqObj!!.myFriends != null) {
                        currentUserReqObj!!.myFriends?.forEach {
                            val user = databaseService.fetchUserByUid(it, object : IOnCompleteListener{
                                override fun onComplete(successful: Boolean, exception: java.lang.Exception?) {
                                    showLoadingView(false)

                                }
                            })
                            friendsList.add(user!!)
                            doesUserHaveAnyFriends.value = friendsList.isNotEmpty()
                        }
                    }

                }
                doesUserHaveAnyFriends.value = friendsList.isNotEmpty()
                friendsAdapter!!.notifyDataSetChanged()

                doesUserHaveAnyRequests.value = friendsRequestList.isNotEmpty()
                friendsRequestAdapter!!.notifyDataSetChanged()
            }

            showLoading(false)
        }
    }


    private fun showLoading(isVisible: Boolean) {
        showLoadingView(isVisible)
    }

    private fun showFriendProfile(userObj: UserObj) {
        Log.d("click", "show friend profile")
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
        viewModelScope.launch {
            println("request declined")
            databaseService.declineRequest(currentUser!!.uid, userObj.uid!!)
        }
    }
}