package com.buzuriu.dogapp.viewModels

import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.models.UserInfo
import com.buzuriu.dogapp.views.EditAccountActivity


class AccountDetailViewModel : BaseViewModel() {
    var user = MutableLiveData<UserInfo>()

    init {
        user.value = localDatabaseService.get("currentUser")
    }

    override fun onResume()
    {
        super.onResume()
        val editedAccount = dataExchangeService.get<UserInfo>(this::class.java.name)
        if(editedAccount != null)
        {
            user.value = editedAccount!!
            localDatabaseService.add("currentUser", user.value!!)
        }
    }

    fun editUser()
    {
        dataExchangeService.put(EditAccountViewModel::class.java.name, user.value!!)
        navigationService.navigateToActivity(EditAccountActivity::class.java)
    }
}