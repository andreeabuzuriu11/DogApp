package com.buzuriu.dogapp.viewModels.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.UserInfo
import com.buzuriu.dogapp.utils.StringUtils
import com.buzuriu.dogapp.views.auth.LoginActivity
import com.buzuriu.dogapp.views.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegisterViewModel : BaseAuthViewModel() {

    var name = MutableLiveData<String>()
    var phone = MutableLiveData<String>()
    var passwordRepeat = MutableLiveData<String>()

    fun loginClick()
    {
        navigationService.navigateToActivity(LoginActivity::class.java, true)
    }

    fun registerClick() {
        if (!fieldsAreCompleted()) return

        ShowLoadingView(true)
        viewModelScope.launch(Dispatchers.IO) {

            firebaseAuthService.registerWithEmailAndPassword(email.value!!, password.value!!,
                object : IOnCompleteListener {
                    override fun onComplete(successful: Boolean, exception: Exception?) {
                        ShowLoadingView(false)

                        if (successful) {

                            val user = databaseService.fireAuth.currentUser
                            if (user != null) {
                                val userInfo = UserInfo(
                                    email.value,
                                    name.value,
                                    phone.value
                                )
                                ShowLoadingView(true)
                                viewModelScope.launch(Dispatchers.IO) {

                                    databaseService.storeUserInfo(
                                        user.uid,
                                        userInfo,
                                        object : IOnCompleteListener {
                                            override fun onComplete(successful: Boolean, exception: Exception?) {
                                                ShowLoadingView(false)

                                                if (successful) {
                                                    dialogService.showSnackbar(R.string.info_added)
                                                    viewModelScope.launch(Dispatchers.Main) {
                                                        delay(1000)
                                                        navigationService.navigateToActivity(MainActivity::class.java, true)
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

                        }
                        else {
                            if (!exception?.message.isNullOrEmpty())
                                dialogService.showSnackbar(exception!!.message!!)
                            dialogService.showSnackbar(R.string.unknown_error)
                        }

                    }
                })
        }
    }

    private fun fieldsAreCompleted(): Boolean {
        if(!connectivityService.isInternetAvailable())
        {
            dialogService.showSnackbar(R.string.no_internet_message)
            return false
        }

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
        if(name.value.isNullOrEmpty())
        {
            dialogService.showSnackbar("Name field is mandatory")
            return false
        }
        if(!StringUtils.isLetters(name.value!!))
        {
            dialogService.showSnackbar("Name cannot contain digits")
            return false
        }

        if(phone.value.isNullOrEmpty())
        {
            dialogService.showSnackbar("Phone field is mandatory")
            return false
        }
        if(phone.value!!.length < 10)
        {
            dialogService.showSnackbar("Phone number cannot be this short")
            return false
        }

        return true
    }
}
