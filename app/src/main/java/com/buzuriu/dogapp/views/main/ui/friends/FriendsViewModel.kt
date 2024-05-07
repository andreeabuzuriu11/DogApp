package com.buzuriu.dogapp.views.main.ui.friends

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.adapters.FriendRequestAdapter
import com.buzuriu.dogapp.adapters.FriendsAdapter
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.RequestObj
import com.buzuriu.dogapp.models.UserObj
import com.buzuriu.dogapp.viewModels.BaseViewModel
import com.buzuriu.dogapp.viewModels.FriendProfileViewModel
import com.buzuriu.dogapp.views.AddFriendActivity
import com.buzuriu.dogapp.views.FriendProfileActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FriendsViewModel : BaseViewModel() {

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
    public suspend fun fetchMyFriendAndFriendsRequest() {
        friendsList.clear()
        friendsRequestList.clear()
        showLoadingView(true)
        viewModelScope.launch(Dispatchers.IO) {
            currentUserReqObj = databaseService.fetchRequestObj(currentUser!!.uid, object :
                IOnCompleteListener {
                override fun onComplete(successful: Boolean, exception: Exception?) {
                    println("does this gets called")
                }
            })

            viewModelScope.launch(Dispatchers.Main) {
                if (currentUserReqObj != null) {
                    if (currentUserReqObj!!.friendsRequests != null) {
                        currentUserReqObj!!.friendsRequests?.forEach {
                            val user =
                                databaseService.fetchUserByUid(it, object : IOnCompleteListener {
                                    override fun onComplete(
                                        successful: Boolean,
                                        exception: java.lang.Exception?
                                    ) {
                                        showLoadingView(false)
                                    }
                                })
                            friendsRequestList.add(user!!)
                            doesUserHaveAnyRequests.value = friendsRequestList.isNotEmpty()

                        }
                    }
                    if (currentUserReqObj!!.myFriends != null) {
                        currentUserReqObj!!.myFriends?.forEach {
                            val user =
                                databaseService.fetchUserByUid(it, object : IOnCompleteListener {
                                    override fun onComplete(
                                        successful: Boolean,
                                        exception: java.lang.Exception?
                                    ) {
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
        exchangeInfoService.put(FriendProfileViewModel::class.java.name, userObj)
        navigationService.navigateToActivity(FriendProfileActivity::class.java, false)
    }

    fun searchFriends() {
        navigationService.navigateToActivity(AddFriendActivity::class.java, false)
    }

    fun acceptRequest(userObj: UserObj) {
        viewModelScope.launch {
            var wasRequestAcceptedListener = object : IOnCompleteListener {
                override fun onComplete(successful: Boolean, exception: java.lang.Exception?) {
                    snackMessageService.displaySnackBar(userObj.name + " was successfully added to your list")
                    friendsRequestList.remove(userObj)
                    friendsRequestAdapter!!.notifyDataSetChanged()
                }
            }
            databaseService.acceptRequest(
                currentUser!!.uid,
                userObj.uid!!,
                wasRequestAcceptedListener
            )

        }
    }

    fun declineRequest(userObj: UserObj) {
        var wasRequestDeclinedListener = object : IOnCompleteListener {
            override fun onComplete(successful: Boolean, exception: java.lang.Exception?) {
                snackMessageService.displaySnackBar("Request from " + userObj.name + " successfuly declined")
                friendsRequestList.remove(userObj)
                friendsRequestAdapter!!.notifyDataSetChanged()
            }
        }
        viewModelScope.launch {
            databaseService.declineRequest(currentUser!!.uid, userObj.uid!!, wasRequestDeclinedListener)
        }
    }
}