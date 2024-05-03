package com.buzuriu.dogapp.viewModels.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.*
import com.buzuriu.dogapp.utils.LocalDBItems
import com.buzuriu.dogapp.utils.StringUtils
import com.buzuriu.dogapp.viewModels.BaseViewModel
import com.buzuriu.dogapp.views.auth.ForgotPasswordActivity
import com.buzuriu.dogapp.views.auth.RegisterActivity
import com.buzuriu.dogapp.views.main.MainActivity
import kotlinx.coroutines.*

class LoginViewModel : BaseViewModel() {

    var email = MutableLiveData("")
    var password = MutableLiveData("")

    fun registerClicked() {
        navigationService.navigateToActivity(RegisterActivity::class.java, true)
    }

    fun loginClicked() {
        if (!fieldsAreCompleted()) return

        showLoadingView(true)
        viewModelScope.launch(Dispatchers.IO) {

            firebaseAuthService.login(email.value!!, password.value!!,
                object : IOnCompleteListener {
                    override fun onComplete(successful: Boolean, exception: Exception?) {

                        showLoadingView(false)

                        if (successful) {
                            viewModelScope.launch(Dispatchers.IO) {
                                delay(1000)
                                async {
                                    prepareForMain()
                                    getUserAccountInfo()
                                    getAllMeetingsThatUserJoined()
                                    navigationService.navigateToActivity(
                                        MainActivity::class.java,
                                        true
                                    )
                                }
                            }
                        } else {
                            if (!exception?.message.isNullOrEmpty())
                                snackMessageService.displaySnackBar(exception!!.message!!)
                            else snackMessageService.displaySnackBar(R.string.unknown_error)

                        }
                    }
                })
        }
    }

    fun forgetPasswordClicked() {
        navigationService.navigateToActivity(ForgotPasswordActivity::class.java, true)
    }

    private fun fieldsAreCompleted(): Boolean {
        if (!internetService.isInternetAvailable()) {
            snackMessageService.displaySnackBar(R.string.no_internet_message)
            return false
        }

        if (email.value.isNullOrEmpty()) {
            snackMessageService.displaySnackBar(R.string.email_missing_message)
            return false
        }

        if (!StringUtils.isEmailValid(email.value!!)) {
            snackMessageService.displaySnackBar(R.string.wrong_email_format_message)
            return false
        }

        if (password.value.isNullOrEmpty()) {
            snackMessageService.displaySnackBar(R.string.password_missing_message)
            return false
        }

        return true
    }

    private suspend fun prepareForMain() {
        val userDogs = databaseService.fetchUserDogs(currentUser!!.uid)
        if (userDogs != null) {
            localDatabaseService.add(LocalDBItems.localDogsList, userDogs)
        }
        var user: UserObj?
        var dog: DogObj?
        val allCustomMeetings = ArrayList<MyCustomMeetingObj>()

        val userMeetings: ArrayList<MeetingObj>? =
            databaseService.fetchUserMeetings(currentUser!!.uid, object : IOnCompleteListener{
                override fun onComplete(successful: Boolean, exception: java.lang.Exception?) {

                }
            })

        if (userMeetings != null) {
            for (meeting in userMeetings) {

                user = databaseService.fetchUserByUid(meeting.userUid!!, object : IOnCompleteListener{
                    override fun onComplete(successful: Boolean, exception: java.lang.Exception?) {

                    }
                })
                dog = databaseService.fetchDogByUid(meeting.dogUid!!)

                if (dog != null) {
                    try {
                        val meetingObj = MyCustomMeetingObj(meeting, user!!, dog)
                        allCustomMeetings.add(meetingObj)
                    }
                    catch (ex:Exception)
                    {
                        println("Exception was caught. ")
                        println("Exception user = " + user!!.name)
                        println("Exception dog = " + user!!.name)
                    }

                }
            }

            localDatabaseService.add(LocalDBItems.localMeetingsList, allCustomMeetings)
        }
    }

    private suspend fun getAllMeetingsThatUserJoined() {
        var allMeetingsParticipants: ArrayList<ParticipantObj>
        val allMeetingsThatUserJoined = ArrayList<MyCustomMeetingObj>()
        var user: UserObj?
        var dog: DogObj?
        val allOtherMeetings: ArrayList<MeetingObj> =
            databaseService.fetchAllOtherMeetings(currentUser!!.uid, object : IOnCompleteListener {
                override fun onComplete(successful: Boolean, exception: java.lang.Exception?) {
                    Log.d("DEBUG", "All meetings have been downloaded with success.")
                }
            })!!

        for (meeting in allOtherMeetings) {
            allMeetingsParticipants =
                databaseService.fetchAllMeetingParticipants(meeting.uid!!)!!
            for (participant in allMeetingsParticipants)
                if (participant.userUid == currentUser!!.uid) {
                    user = databaseService.fetchUserByUid(meeting.userUid!!, object : IOnCompleteListener{
                        override fun onComplete(successful: Boolean, exception: java.lang.Exception?) {

                        }
                    })
                    dog = databaseService.fetchDogByUid(meeting.dogUid!!)

                    if (dog != null) {
                        val meetingObj = MyCustomMeetingObj(meeting, user!!, dog)
                        allMeetingsThatUserJoined.add(meetingObj)
                    }
                }
        }
        localDatabaseService.add(LocalDBItems.meetingsUserJoined, allMeetingsThatUserJoined)
    }

    private suspend fun getUserAccountInfo() {
        val userObj: UserObj? = databaseService.fetchUserByUid(currentUser!!.uid, object : IOnCompleteListener{
            override fun onComplete(successful: Boolean, exception: java.lang.Exception?) {

            }
        })

        if (userObj != null)
            localDatabaseService.add(LocalDBItems.currentUser, userObj)
        else {
            snackMessageService.displaySnackBar("Error, this user has been deleted!")
            delay(3000)
            navigationService.navigateToActivity(RegisterActivity::class.java, true)
        }
    }

}