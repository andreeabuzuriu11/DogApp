package com.buzuriu.dogapp.viewModels

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.listeners.IClickListener
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.CountryObj
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import com.buzuriu.dogapp.models.UserObj
import com.buzuriu.dogapp.services.ILocalDatabaseService
import com.buzuriu.dogapp.utils.LocalDBItems
import com.buzuriu.dogapp.views.EditAccountActivity
import com.buzuriu.dogapp.views.SelectCountryFragment
import com.buzuriu.dogapp.views.auth.LoginActivity
import com.buzuriu.dogapp.views.main.ui.OverlayActivity
import com.buzuriu.dogapp.views.main.ui.my_dogs.MyDogsViewModel
import java.lang.Exception


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
        firebaseAnalyticsService.logEvent("Event", "UserEvent", "Edit_Profile")

        exchangeInfoService.put(EditAccountViewModel::class.java.name, user.value!!)
        navigationService.navigateToActivity(EditAccountActivity::class.java)
    }

    fun deleteUser() {
        alertMessageService.displayAlertDialog(
            "Delete account?",
            "Are you sure you want to delete your account? This action cannot be undone.",
            "Yes, delete it",
            object :
                IClickListener {
                override fun clicked() {
                   firebaseAuthService.deleteAccount(object : IOnCompleteListener{
                       override fun onComplete(successful: Boolean, exception: Exception?) {
                           val countriesList =
                               localDatabaseService.get<ArrayList<CountryObj>>(LocalDBItems.countries)!!

                           // todo delete user meetings
                           // todo delete user as participant for all meetings

                           localDatabaseService.clear()
                           localDatabaseService.add(LocalDBItems.countries, countriesList)

                           navigationService.navigateToActivity(LoginActivity::class.java, true)
                           firebaseAuthService.logout()
                       }
                   });
                }
            })
    }


}