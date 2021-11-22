package com.buzuriu.dogapp.viewModels.auth

import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.viewModels.BaseViewModel

open class BaseAuthViewModel : BaseViewModel() {

    protected val minPasswordLength = 6
    var email = MutableLiveData<String>()
    var password = MutableLiveData<String>()
}