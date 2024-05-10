package com.buzuriu.dogapp.viewModels.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.models.LocationObj
import com.buzuriu.dogapp.models.UserObj
import com.buzuriu.dogapp.utils.LocalDBItems
import com.buzuriu.dogapp.utils.StringUtils
import com.buzuriu.dogapp.views.SelectCountryFragment
import com.buzuriu.dogapp.views.auth.LoginActivity
import com.buzuriu.dogapp.views.auth.RegisterActivity
import com.buzuriu.dogapp.views.main.MainActivity
import com.buzuriu.dogapp.views.main.ui.OverlayActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class RegisterViewModel : BaseAuthViewModel() {

    var name = MutableLiveData<String>()
    var phone = MutableLiveData<String>()
    var city = MutableLiveData<String>()
    var passwordRepeat = MutableLiveData<String>()
    var isFemaleGenderSelected = MutableLiveData<Boolean>()
    private var currentGenderString: String? = null

    fun loginClick() {
        navigationService.navigateToActivity(LoginActivity::class.java, true)
    }

    override fun onResume() {
        val selectedCity = exchangeInfoService.get<LocationObj>(this::class.qualifiedName!!)
        if (selectedCity != null) {
            city.value = selectedCity.toString()
        }
    }



    fun registerClick() {
        if (!fieldsAreCompleted()) return

        showLoadingView(true)
        currentGenderString = if (isFemaleGenderSelected.value!!) {
            "female"
        } else
            "male"
        viewModelScope.launch(Dispatchers.IO) {

            firebaseAuthService.registerWithEmailAndPassword(email.value!!, password.value!!,
                object : IOnCompleteListener {
                    override fun onComplete(successful: Boolean, exception: Exception?) {
                        showLoadingView(false)

                        if (successful) {

                            val user = databaseService.fireAuth.currentUser
                            if (user != null) {
                                val userObj = UserObj(
                                    user.uid,
                                    email.value,
                                    name.value,
                                    phone.value,
                                    currentGenderString!!,

                                    )
                                showLoadingView(true)
                                viewModelScope.launch(Dispatchers.IO) {

                                    databaseService.storeUser(
                                        user.uid,
                                        userObj,
                                        object : IOnCompleteListener {
                                            override fun onComplete(
                                                successful: Boolean,
                                                exception: Exception?
                                            ) {
                                                showLoadingView(false)

                                                if (successful) {
                                                    snackMessageService.displaySnackBar(R.string.info_added)
                                                    viewModelScope.launch(Dispatchers.Main) {
                                                        getUserAccountInfo()
                                                        localDatabaseService.add(
                                                            LocalDBItems.localDogsList,
                                                            ArrayList<DogObj>()
                                                        )
                                                        delay(1000)
                                                        navigationService.navigateToActivity(
                                                            MainActivity::class.java,
                                                            true
                                                        )
                                                    }
                                                } else {
                                                    if (!exception?.message.isNullOrEmpty())
                                                        snackMessageService.displaySnackBar(
                                                            exception!!.message!!
                                                        )
                                                    else snackMessageService.displaySnackBar(R.string.unknown_error)
                                                }
                                            }
                                        })
                                }
                            }

                        } else {
                            if (!exception?.message.isNullOrEmpty())
                                snackMessageService.displaySnackBar(exception!!.message!!)
                            snackMessageService.displaySnackBar(R.string.unknown_error)
                        }
                    }
                })
        }
    }

    private fun fieldsAreCompleted(): Boolean {
        if (!checkInternetConnection())
            return false

        if (!checkIfFieldsAreFilledOut())
            return false

        if (!checkEmailIsValid())
            return false

        if (!checkPasswordIsMatching())
            return false

        if (!checkNameIsValid())
            return false

        if (!checkPhoneIsValid())
            return false

        return true
    }

    private fun checkNameIsValid(): Boolean {
        if (!StringUtils.isLetters(name.value!!) && !name.value!!.contains(' ')) {
            snackMessageService.displaySnackBar("Name cannot contain digits")
            return false
        }
        return true
    }

    private fun checkPhoneIsValid(): Boolean {
        if (phone.value!!.length < 10) {
            snackMessageService.displaySnackBar("Phone number cannot be this short")
            return false
        }
        return true
    }

    private fun checkInternetConnection(): Boolean {
        if (!internetService.isInternetAvailable()) {
            snackMessageService.displaySnackBar(R.string.no_internet_message)
            return false
        }
        return true
    }

    private fun checkIfFieldsAreFilledOut(): Boolean {
        if (email.value.isNullOrEmpty()) {
            snackMessageService.displaySnackBar(R.string.email_missing_message)
            return false
        }

        if (password.value.isNullOrEmpty() || passwordRepeat.value.isNullOrEmpty()) {
            snackMessageService.displaySnackBar(R.string.password_missing_message)
            return false
        }

        if (name.value.isNullOrEmpty()) {
            snackMessageService.displaySnackBar("Name field is mandatory")
            return false
        }

        if (phone.value.isNullOrEmpty()) {
            snackMessageService.displaySnackBar("Phone field is mandatory")
            return false
        }
        return true
    }

    private fun checkPasswordIsMatching(): Boolean {
        if (!password.value.equals(passwordRepeat.value)) {
            snackMessageService.displaySnackBar(R.string.password_does_not_match_message)
            return false
        }

        if (password.value!!.length < minPasswordLength) {
            snackMessageService.displaySnackBar(R.string.password_short_message)
            return false
        }
        return true
    }

    private fun checkEmailIsValid(): Boolean {
        if (!StringUtils.isEmailValid(email.value!!)) {
            snackMessageService.displaySnackBar(R.string.wrong_email_format_message)
            return false
        }
        return true
    }

    private suspend fun getUserAccountInfo() {
        val userObj: UserObj? =
            databaseService.fetchUserByUid(currentUser!!.uid, object : IOnCompleteListener {
                override fun onComplete(successful: Boolean, exception: java.lang.Exception?) {

                }
            })

        if (userObj != null)
            localDatabaseService.add(LocalDBItems.currentUser, userObj)
        else {
            snackMessageService.displaySnackBar("Error, this user has been deleted!")
            delay(3000)
            navigationService.navigateToActivity(RegisterActivity::class.java, true)
        }
    }

    fun selectCity() {
        navigationService.showOverlay(
            OverlayActivity::class.java,
            false,
            LocalDBItems.fragmentName,
            SelectCountryFragment::class.qualifiedName
        )
    }

}
