package com.buzuriu.dogapp.viewModels.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.viewModels.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : BaseViewModel() {

    var email = MutableLiveData("")

    fun sendPasswordResetRequest() {
        if(!fieldsAreCompleted())return

        ShowLoadingView(true)

        viewModelScope.launch(Dispatchers.IO) {
            firebaseAuthService.resetPassword(email.value!!,  object : IOnCompleteListener {
                override fun onComplete(successful: Boolean, exception: Exception?) {

                    ShowLoadingView(false)

                    if (successful) {
                        viewModelScope.launch(Dispatchers.IO){
                            dialogService.showSnackbar(R.string.forgot_password_message)
                            delay(1000)
                            navigationService.closeCurrentActivity()
                        }
                    } else {
                        if (!exception?.message.isNullOrEmpty())
                            dialogService.showSnackbar(exception!!.message!!)
                        else dialogService.showSnackbar(R.string.unknown_error)

                    }
                }
            })
        }
    }
    private fun fieldsAreCompleted(): Boolean {

        if (!connectivityService.isInternetAvailable()) {
            dialogService.showSnackbar(R.string.no_internet_message)
            return false
        }

        if (email.value.isNullOrEmpty()) {
            dialogService.showSnackbar(R.string.email_missing_message)
            return false
        }

        return true
    }
}