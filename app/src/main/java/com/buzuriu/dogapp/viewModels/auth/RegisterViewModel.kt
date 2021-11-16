package com.buzuriu.dogapp.viewModels.auth

import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.viewModels.BaseViewModel

class RegisterViewModel : BaseViewModel() {

    var email = MutableLiveData("")
    var password = MutableLiveData("")
    var passwordRepeat = MutableLiveData("")

    fun loginClick()
    {

    }
}
