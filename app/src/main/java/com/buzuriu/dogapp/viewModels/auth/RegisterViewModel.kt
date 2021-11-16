package com.buzuriu.dogapp.viewModels.auth

import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.viewModels.BaseViewModel
import com.buzuriu.dogapp.views.auth.LoginActivity
import com.buzuriu.dogapp.views.auth.RegisterActivity

class RegisterViewModel : BaseViewModel() {

    var name = MutableLiveData("")
    var phone = MutableLiveData("")
    var email = MutableLiveData("")
    var password = MutableLiveData("")
    var passwordRepeat = MutableLiveData("")

    fun loginClick()
    {
        navigationService.navigateToActivity(LoginActivity::class.java, true)
    }
}
