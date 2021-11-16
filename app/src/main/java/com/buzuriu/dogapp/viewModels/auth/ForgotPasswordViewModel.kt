package com.buzuriu.dogapp.viewModels.auth

import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.viewModels.BaseViewModel

class ForgotPasswordViewModel : BaseViewModel() {

    var email = MutableLiveData("")

    fun sendPasswordResetRequest() {

    }
}