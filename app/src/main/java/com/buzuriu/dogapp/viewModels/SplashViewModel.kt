package com.buzuriu.dogapp.viewModels

import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.listeners.IGetUserDogListListener
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.models.UserInfo
import com.buzuriu.dogapp.views.auth.RegisterActivity
import com.buzuriu.dogapp.views.main.MainActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel : BaseViewModel() {

    init {
        // Check if authentication might work
        // var auth: FirebaseAuth
        // auth = Firebase.auth
        // auth.signInAnonymously()
        // val t = auth.currentUser
        if(Firebase.auth.currentUser!=null)
        {
            viewModelScope.launch(Dispatchers.IO) {
                delay(1000)
                async {
                    prepareForMain()
                    navigationService.navigateToActivity(MainActivity::class.java, true)
                }}
        }
        else {
            viewModelScope.launch(Dispatchers.IO) {
                delay(1000)
                navigationService.navigateToActivity(RegisterActivity::class.java, true)
            }
        }
    }

    private suspend fun  prepareForMain()
    {
/*        var dog : DogObj? =  null
        var user : UserInfo? = null

            var z = databaseService.fetchUserByUid(firebaseAuthService.getCurrentUser()!!.uid)

            var g=z;
            return
            databaseSe*/
        databaseService.fetchUserDogs(currentUser!!.uid, object: IGetUserDogListListener {
                override fun getDogList(dogList: ArrayList<DogObj>) {
                    localDatabaseService.add("localDogsList", dogList)










                    viewModelScope.launch {
                        /*databaseService.fetchAllMeetings(object : IGetMeetingListListener {
                            override fun getMeetingList(meetingList: ArrayList<MeetingObj>) {
                                localDatabaseService.add("localMeetings", meetingList)
                                for (meeting in meetingList)
                                {
                                    viewModelScope.launch {
                                        user =  databaseService.fetchUserByUid(meeting.userUid!!, object : IOnCompleteListener
                                        {
                                            override fun onComplete(
                                                successful: Boolean,
                                                exception: Exception?
                                            ) {
                                                Log.d("andreea", user.toString())
                                            }
                                        })
                                    }
                                    viewModelScope.launch {
                                        dog = databaseService.fetchDogByUid(meeting.dogUid!!, object : IOnCompleteListener
                                        {
                                            override fun onComplete(
                                                successful: Boolean,
                                                exception: Exception?
                                            ) {
                                                Log.d("andreea", dog.toString())
                                            }
                                        })
                                    }
                                }
                            }
                        })*/
                    }
                }
            })
    }
}