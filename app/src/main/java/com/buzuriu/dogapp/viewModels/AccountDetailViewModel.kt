package com.buzuriu.dogapp.viewModels

import com.buzuriu.dogapp.models.UserInfo


class AccountDetailViewModel : BaseViewModel() {
    var user : UserInfo? = null

    init {
        user = localDatabaseService.get("currentUser")
    }
}