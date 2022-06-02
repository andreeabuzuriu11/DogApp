package com.buzuriu.dogapp.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.MeetingObj
import com.buzuriu.dogapp.models.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EditAccountViewModel : BaseViewModel() {

    var user = MutableLiveData<UserInfo>()
    var isFemaleGenderSelected = MutableLiveData<Boolean>()
    var currentGenderString: String? = null

    init {
        user.value = dataExchangeService.get<UserInfo>(this::class.java.name)
        checkUserGender(user.value!!.gender!!)
    }

    private fun checkUserGender(gender: String) {
        if (gender == "female")
            isFemaleGenderSelected.value = true
        else if (gender == "male")
            isFemaleGenderSelected.value = false
    }

    fun editAccount() {
        currentGenderString = if (isFemaleGenderSelected.value!!) {
            "female"
        } else
            "male"

        val user = UserInfo(
            user.value!!.email,
            user.value!!.name,
            user.value!!.phone,
            currentGenderString
        )
        viewModelScope.launch(Dispatchers.IO)
        {
            databaseService.storeUserInfo(currentUser!!.uid, user, object : IOnCompleteListener {
                override fun onComplete(successful: Boolean, exception: Exception?) {

                    if (successful) {
                        viewModelScope.launch(Dispatchers.Main) {
                            snackMessageService.displaySnackBar("Edited successful")
                            dataExchangeService.put(AccountDetailViewModel::class.java.name, user)
                            delay(2000)
                            changeMeetingInfoRelatedToThisUser()
                            navigationService.closeCurrentActivity()
                        }
                    } else {
                        viewModelScope.launch(Dispatchers.Main) {
                            if (!exception?.message.isNullOrEmpty())
                                snackMessageService.displaySnackBar(exception!!.message!!)
                            else snackMessageService.displaySnackBar(R.string.unknown_error)
                            delay(2000)
                        }
                    }

                    ShowLoadingView(false)
                }
            })
        }
    }

    fun changeMeetingInfoRelatedToThisUser() {
        var meetings = ArrayList<MeetingObj>()
        viewModelScope.launch(Dispatchers.IO) {
            meetings = databaseService.fetchUserMeetings(currentUser!!.uid)!!

            viewModelScope.launch(Dispatchers.Main) {
                for (meeting in meetings) {
                    meeting.userGender = currentGenderString

                    databaseService.storeMeetingInfo(
                        meeting.uid!!,
                        meeting,
                        object : IOnCompleteListener {
                            override fun onComplete(successful: Boolean, exception: Exception?) {

                                if (successful) {
                                    viewModelScope.launch(Dispatchers.Main) {
                                        snackMessageService.displaySnackBar("All meetings have been updated with the new info")
                                        delay(2000)
                                    }
                                } else {
                                    viewModelScope.launch(Dispatchers.Main) {
                                        if (!exception?.message.isNullOrEmpty())
                                            snackMessageService.displaySnackBar(exception!!.message!!)
                                        else snackMessageService.displaySnackBar(R.string.unknown_error)
                                        delay(2000)
                                    }
                                }
                            }
                        })
                }
            }
        }
    }
}