package com.buzuriu.dogapp.viewModels

import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.views.auth.RegisterActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel : BaseViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            delay(1000)
            navigationService.navigateToActivity(RegisterActivity::class.java, true)
        }
    }
}