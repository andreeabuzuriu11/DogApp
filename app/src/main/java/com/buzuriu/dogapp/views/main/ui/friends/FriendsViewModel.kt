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
        userAdapter = UserAdapter(foundUsersList)
    }

    fun findUser(searchedUserText: String) {

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
        }


        if (foundUser != null) {
            if (!isUserAlreadyFound(foundUser!!))
                foundUsersList.add(foundUser!!)
            if (!isUserMatchingWithCurrentSearch(foundUser!!, searchedUserText))
                foundUsersList.remove(foundUser)
        }

        if (isSearchedTextEmpty(searchedUserText))
            foundUsersList.removeAll(foundUsersList.toSet())

        userAdapter!!.notifyDataSetChanged()
    }


    private fun isUserAlreadyFound(userObj: UserObj): Boolean {
        if (foundUsersList.contains(userObj))
            return true
        return false
    }

    private fun isUserMatchingWithCurrentSearch(userObj: UserObj, searchedUserText: String): Boolean {
        if (userObj.email!!.contains(searchedUserText))
            return true
        if (userObj.name!!.contains(searchedUserText))
            return true
        return false
    }

    private fun isSearchedTextEmpty(searchedUserText: String): Boolean {
        if (searchedUserText.length == 1 || searchedUserText == "")
            return true
        return false
    }

}