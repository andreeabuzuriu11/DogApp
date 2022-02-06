package com.buzuriu.dogapp.viewModels

import android.util.Log
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
        if (Firebase.auth.currentUser != null) {
            viewModelScope.launch(Dispatchers.IO) {
                delay(1000)
                async {
                    prepareForMain()
                    navigationService.navigateToActivity(MainActivity::class.java, true)
                }
            }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                delay(1000)
                navigationService.navigateToActivity(RegisterActivity::class.java, true)
            }
        }
    }

    private suspend fun prepareForMain() {
        val userDogs = databaseService.fetchUserDogs(currentUser!!.uid)
        if (userDogs != null) {
            localDatabaseService.add("localDogsList", userDogs)
        }
        var user:UserInfo? = null
        var dog:DogObj? = null

        val allMeetings = databaseService.fetchAllMeetings()
        if (allMeetings != null) {
            localDatabaseService.add("localMeetings", allMeetings)
        }
        if (allMeetings != null) {
            for (meeting in allMeetings)
            {
                user = databaseService.fetchUserByUid(meeting.userUid!!)
                dog = databaseService.fetchDogByUid(meeting.dogUid!!)
                Log.d("dogapp", user.toString())
                Log.d("dogapp",dog.toString())

            }
        }
    }
}
