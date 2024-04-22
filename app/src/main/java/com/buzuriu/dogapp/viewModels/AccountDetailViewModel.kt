package com.buzuriu.dogapp.viewModels

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.models.UserObj
import com.buzuriu.dogapp.utils.LocalDBItems
import com.buzuriu.dogapp.views.EditAccountActivity


class AccountDetailViewModel : BaseViewModel() {
    var user = MutableLiveData<UserObj>()
    var userBitmapImage = MutableLiveData<Bitmap>()
    var userImageUrl = MutableLiveData<String>()

    init {
        user.value = localDatabaseService.get(LocalDBItems.currentUser)

        if (user.value?.imageUrl != null)
            userImageUrl.value = user.value?.imageUrl

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