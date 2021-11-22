package com.buzuriu.dogapp.viewModels.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.utils.StringUtils
import com.buzuriu.dogapp.viewModels.BaseViewModel
import com.buzuriu.dogapp.views.auth.LoginActivity
import com.buzuriu.dogapp.views.auth.RegisterActivity
import com.buzuriu.dogapp.views.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterViewModel : BaseAuthViewModel() {

    var name = MutableLiveData("")
    var phone = MutableLiveData("")
    var passwordRepeat = MutableLiveData("")

    fun loginClick()
    {
        navigationService.navigateToActivity(LoginActivity::class.java, true)
    }

    fun registerClick() {
        if (!fieldsAreCompleted()) return

        //TODO show a loading view
        viewModelScope.launch(Dispatchers.IO) {

            firebaseAuthService.registerWithEmailAndPassword(email.value!!, password.value!!,
                object : IOnCompleteListener {
                    override fun onComplete(successful: Boolean, exception: Exception?) {

                        //TODO stop showing loading view if onComplete

                        if (successful)
                        {
                            navigationService.navigateToActivity(MainActivity::class.java, true)
                        }
                        else
                        {
                            if (!exception?.message.isNullOrEmpty())
                                dialogService.showSnackbar(exception!!.message!!)
                            else dialogService.showSnackbar(R.string.unknown_error)
                        }
                    }
                })
        }
    }

    private fun fieldsAreCompleted(): Boolean {

        //TODO check it user is connected to internet

        if (email.value.isNullOrEmpty()) {
            dialogService.showSnackbar(R.string.email_missing_message)
            return false
        }

        if (!StringUtils.isEmailValid(email.value!!)) {
            dialogService.showSnackbar(R.string.wrong_email_format_message)
            return false
        }

        if (password.value.isNullOrEmpty() || passwordRepeat.value.isNullOrEmpty()) {
            dialogService.showSnackbar(R.string.password_missing_message)
            return false
        }

        if (!password.value.equals(passwordRepeat.value)) {
            dialogService.showSnackbar(R.string.password_does_not_match_message)
            return false
        }

        if(password.value!!.length < minPasswordLength)
        {
            dialogService.showSnackbar(R.string.password_short_message)
            return false
        }

        return true
    }
}
