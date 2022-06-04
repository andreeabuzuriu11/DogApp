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

        showLoadingView(true)

        viewModelScope.launch(Dispatchers.IO) {
            firebaseAuthService.resetPassword(email.value!!,  object : IOnCompleteListener {
                override fun onComplete(successful: Boolean, exception: Exception?) {

                    showLoadingView(false)

                    if (successful) {
                        viewModelScope.launch(Dispatchers.IO){
                            snackMessageService.displaySnackBar(R.string.forgot_password_message)
                            delay(1000)
                            navigationService.closeCurrentActivity()
                        }
                    } else {
                        if (!exception?.message.isNullOrEmpty())
                            snackMessageService.displaySnackBar(exception!!.message!!)
                        else snackMessageService.displaySnackBar(R.string.unknown_error)

                    }
                }
            })
        }
    }
    private fun fieldsAreCompleted(): Boolean {

        if (!internetService.isInternetAvailable()) {
            snackMessageService.displaySnackBar(R.string.no_internet_message)
            return false
        }

        if (email.value.isNullOrEmpty()) {
            snackMessageService.displaySnackBar(R.string.email_missing_message)
            return false
        }

        return true
    }
}