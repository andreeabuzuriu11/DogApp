package com.buzuriu.dogapp.viewModels

import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.models.UserObj
import com.buzuriu.dogapp.utils.LocalDBItems
import com.buzuriu.dogapp.views.EditAccountActivity


class AccountDetailViewModel : BaseViewModel() {
    var user = MutableLiveData<UserObj>()

    init {
        user.value = localDatabaseService.get(LocalDBItems.currentUser)
    }

    override fun onResume() {
        super.onResume()
        val editedAccount = exchangeInfoService.get<UserObj>(this::class.java.name)
        if (editedAccount != null) {
            user.value = editedAccount!!
            localDatabaseService.add(LocalDBItems.currentUser, user.value!!)
        }
    }

    fun editUser() {
        exchangeInfoService.put(EditAccountViewModel::class.java.name, user.value!!)
        navigationService.navigateToActivity(EditAccountActivity::class.java)
    }
}