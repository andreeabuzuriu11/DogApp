package com.buzuriu.dogapp.viewModels

import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.listeners.IGetMeetingListListener
import com.buzuriu.dogapp.listeners.IGetUserDogListListener
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.models.MeetingObj
import com.buzuriu.dogapp.views.auth.RegisterActivity
import com.buzuriu.dogapp.views.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
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
                prepareForMain()
                navigationService.navigateToActivity(MainActivity::class.java, true)
            }
        }
        else {
            viewModelScope.launch(Dispatchers.IO) {
                delay(1000)
                navigationService.navigateToActivity(RegisterActivity::class.java, true)
            }
        }
    }

    private fun prepareForMain()
    {
        viewModelScope.launch {
            databaseService.fetchUserDogs(currentUser!!.uid, object: IGetUserDogListListener {
                override fun getDogList(dogList: ArrayList<DogObj>) {
                    localDatabaseService.add("localDogsList", dogList)
                    viewModelScope.launch {
                        databaseService.fetchAllMeetings(object : IGetMeetingListListener {
                            override fun getMeetingList(meetingList: ArrayList<MeetingObj>) {
                                localDatabaseService.add("localMeetings", meetingList)
                            }
                        })
                    }
                }
            })
        }
    }
}