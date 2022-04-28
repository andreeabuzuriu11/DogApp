package com.buzuriu.dogapp.viewModels.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.models.MeetingObj
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import com.buzuriu.dogapp.models.UserInfo
import com.buzuriu.dogapp.utils.StringUtils
import com.buzuriu.dogapp.viewModels.BaseViewModel
import com.buzuriu.dogapp.views.auth.ForgotPasswordActivity
import com.buzuriu.dogapp.views.auth.RegisterActivity
import com.buzuriu.dogapp.views.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel : BaseViewModel() {

    var email = MutableLiveData("")
    var password = MutableLiveData("")

    fun registerClicked() {
        navigationService.navigateToActivity(RegisterActivity::class.java, true)
    }

    fun loginClicked() {
        if (!fieldsAreCompleted()) return

        ShowLoadingView(true)
        viewModelScope.launch(Dispatchers.IO) {

            firebaseAuthService.login(email.value!!, password.value!!,
                object : IOnCompleteListener {
                    override fun onComplete(successful: Boolean, exception: Exception?) {

                        ShowLoadingView(false)

                        if (successful) {
                            viewModelScope.launch(Dispatchers.IO) {
                                delay(1000)
                                async {
                                    prepareForMain()
                                    getUserAccountInfo()
                                    navigationService.navigateToActivity(
                                        MainActivity::class.java,
                                        true
                                    )
                                }
                            }
                        } else {
                            if (!exception?.message.isNullOrEmpty())
                                dialogService.showSnackbar(exception!!.message!!)
                            else dialogService.showSnackbar(R.string.unknown_error)

                        }
                    }
                })
        }
    }

    fun forgetPasswordClicked() {
        navigationService.navigateToActivity(ForgotPasswordActivity::class.java, true)
    }

    private fun fieldsAreCompleted(): Boolean {
        if (!connectivityService.isInternetAvailable()) {
            dialogService.showSnackbar(R.string.no_internet_message)
            return false
        }

        if (email.value.isNullOrEmpty()) {
            dialogService.showSnackbar(R.string.email_missing_message)
            return false
        }

        if (!StringUtils.isEmailValid(email.value!!)) {
            dialogService.showSnackbar(R.string.wrong_email_format_message)
            return false
        }

        if (password.value.isNullOrEmpty()) {
            dialogService.showSnackbar(R.string.password_missing_message)
            return false
        }

        return true
    }

    private suspend fun prepareForMain() {
        val userDogs = databaseService.fetchUserDogs(currentUser!!.uid)
        if (userDogs != null) {
            localDatabaseService.add("localDogsList", userDogs)
        }
        var user: UserInfo?
        var dog: DogObj?
        val allCustomMeetings = ArrayList<MyCustomMeetingObj>()

        val userMeetings: ArrayList<MeetingObj>? = databaseService.fetchUserMeetings(currentUser!!.uid)

        if (userMeetings != null) {
            for (meeting in userMeetings) {
                user = databaseService.fetchUserByUid(meeting.userUid!!)
                dog = databaseService.fetchDogByUid(meeting.dogUid!!)

                if (dog != null) {
                    val meetingObj = MyCustomMeetingObj(meeting, user!!, dog)
                    allCustomMeetings.add(meetingObj)
                }
            }

            localDatabaseService.add("localMeetingsList", allCustomMeetings)
        }
    }

    private suspend fun getUserAccountInfo() {
        val userInfo : UserInfo? = databaseService.fetchUserByUid(currentUser!!.uid)

        localDatabaseService.add("currentUser", userInfo!!)
    }

}