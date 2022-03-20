package com.buzuriu.dogapp.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.models.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.getScopeName


class EditAccountViewModel : BaseViewModel() {

    var user = MutableLiveData<UserInfo>()
    var isFemaleGenderSelected = MutableLiveData<Boolean>()
    var currentGenderString : String? = null

    init {
        user.value = dataExchangeService.get<UserInfo>(this::class.java.name)
        checkUserGender(user.value!!.gender!!)
    }

    private fun checkUserGender(gender: String)
    {
        if (gender == "female")
            isFemaleGenderSelected.value = true
        else if (gender == "male")
            isFemaleGenderSelected.value = false
    }

    fun editAccount()
    {
        currentGenderString = if (isFemaleGenderSelected.value!!) {
            "female"
        } else
            "male"

        val user = UserInfo(
            user.value!!.email,
            user.value!!.name,
            user.value!!.phone,
            currentGenderString
        )
        viewModelScope.launch(Dispatchers.IO)
        {
            databaseService.storeUserInfo(currentUser!!.uid, user, object : IOnCompleteListener {
                override fun onComplete(successful: Boolean, exception: Exception?) {

                    if (successful) {
                        viewModelScope.launch(Dispatchers.Main) {
                            dialogService.showSnackbar("Edited successful")
                            dataExchangeService.put(AccountDetailViewModel::class.java.name, user)
                            delay(2000)
                            navigationService.closeCurrentActivity()
                        }
                    } else {
                        viewModelScope.launch(Dispatchers.Main) {
                            if (!exception?.message.isNullOrEmpty())
                                dialogService.showSnackbar(exception!!.message!!)
                            else dialogService.showSnackbar(R.string.unknown_error)
                            delay(2000)
                        }
                    }

                    ShowLoadingView(false)
                }
            })
        }
    }
}