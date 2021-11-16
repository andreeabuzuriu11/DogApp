package com.buzuriu.dogapp.viewModels.auth

import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.viewModels.BaseViewModel
import com.buzuriu.dogapp.views.auth.ForgotPasswordActivity
import com.buzuriu.dogapp.views.auth.RegisterActivity

class LoginViewModel : BaseViewModel() {

    var email = MutableLiveData("")
    var password = MutableLiveData("")

    fun registerClicked() {
        navigationService.navigateToActivity(RegisterActivity::class.java, true)
    }

    fun loginClicked() {

    }

    fun forgetPasswordClicked() {
        navigationService.navigateToActivity(ForgotPasswordActivity::class.java, true)
    }
}