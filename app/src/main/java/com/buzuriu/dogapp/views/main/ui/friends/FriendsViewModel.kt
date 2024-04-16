package com.buzuriu.dogapp.views.main.ui.friends

import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.adapters.DogAdapter
import com.buzuriu.dogapp.adapters.UserAdapter
import com.buzuriu.dogapp.listeners.IClickListener
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.models.UserObj
import com.buzuriu.dogapp.viewModels.BaseViewModel
import com.buzuriu.dogapp.viewModels.DogDetailViewModel
import com.buzuriu.dogapp.views.DogDetailActivity
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FriendsViewModel : BaseViewModel() {

    var foundUser: UserObj? = null
    private var foundUsersList: ArrayList<UserObj> = ArrayList()
    var userAdapter: UserAdapter? = null

    init {

    }

    fun findUser(searchedUserText: String): UserObj? {
        var foundUser: UserObj? = null

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

            if (foundUser == null)
                return@launch

            alertMessageService.displayAlertDialog(
                "found user",
                foundUser?.name!! + foundUser?.phone + foundUser?.gender,
                "ok",
                object :
                    IClickListener {
                    override fun clicked() {
                        // close the alert
                    }
                })
        }

        // TODO fix
        foundUsersList.add(foundUser!!)

        userAdapter = UserAdapter(foundUsersList)
        userAdapter!!.notifyDataSetChanged()
        return foundUser
    }








}