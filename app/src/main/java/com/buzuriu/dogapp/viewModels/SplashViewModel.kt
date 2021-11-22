package com.buzuriu.dogapp.viewModels

import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.views.auth.RegisterActivity
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
        viewModelScope.launch(Dispatchers.IO) {
            delay(1000)
            navigationService.navigateToActivity(RegisterActivity::class.java, true)
        }
    }
}