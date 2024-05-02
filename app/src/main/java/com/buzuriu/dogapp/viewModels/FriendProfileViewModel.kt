package com.buzuriu.dogapp.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.adapters.FriendMeetingAdapter
import com.buzuriu.dogapp.enums.MeetingStateEnum
import com.buzuriu.dogapp.listeners.IClickListener
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import com.buzuriu.dogapp.models.UserObj
import com.buzuriu.dogapp.utils.LocalDBItems
import com.buzuriu.dogapp.views.MeetingDetailActivity
import com.buzuriu.dogapp.views.SelectDogForJoinMeetFragment
import com.buzuriu.dogapp.views.main.ui.OverlayActivity
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FriendProfileViewModel : BaseViewModel() {

    var user = MutableLiveData<UserObj>()

    var userName = MutableLiveData("")
    var userImage = MutableLiveData("")
    var isPlaceholderVisible = MutableLiveData(false)
    private var meetingsList = ArrayList<MyCustomMeetingObj>()

    var eventsAdapter : FriendMeetingAdapter? = null
    init {
        user.value = exchangeInfoService.get<UserObj>(this::class.qualifiedName!!)!!
        eventsAdapter = FriendMeetingAdapter(meetingsList, ::selectedMeeting, this)
    }


    private fun selectedMeeting(myCustomMeetingObj: MyCustomMeetingObj) {
        exchangeInfoService.put(MeetingDetailViewModel::class.java.name, myCustomMeetingObj)
        navigationService.navigateToActivity(MeetingDetailActivity::class.java, false)
    }

}