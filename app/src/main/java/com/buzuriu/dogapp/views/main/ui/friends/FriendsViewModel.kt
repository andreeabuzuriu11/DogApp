package com.buzuriu.dogapp.views.main.ui.friends

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.listeners.IClickListener
import com.buzuriu.dogapp.models.UserObj
import com.buzuriu.dogapp.viewModels.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FriendsViewModel : BaseViewModel() {

    var searchedEmail = MutableLiveData<String>()

    init {

    }

    fun getAllUsers(searchedUserText: String) {
        var foundUser: UserObj = UserObj("")

        viewModelScope.launch(Dispatchers.Main) {
            val allUsers = databaseService.fetchUsers()
            if (allUsers != null) {
                for (user in allUsers)
                    if (user.email!!.contains(searchedUserText))
                        foundUser = user
                    else
                        if (user.name != null && user.name!!.contains(searchedUserText))
                            foundUser = user
            }

            alertMessageService.displayAlertDialog(
                "found user",
                foundUser.name!! + foundUser.phone + foundUser.gender,
                "ok",
                object :
                    IClickListener {
                    override fun clicked() {
                        // close the alert
                    }
                })
        }
    }

}