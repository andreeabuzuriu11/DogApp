package com.buzuriu.dogapp.views.main.ui.friends

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.viewModels.BaseViewModel

class FriendsViewModel : BaseViewModel() {

    var searchedEmail = MutableLiveData<String>()

    init {

    }

}