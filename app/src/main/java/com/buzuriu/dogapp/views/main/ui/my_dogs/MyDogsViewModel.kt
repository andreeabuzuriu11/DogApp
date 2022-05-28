package com.buzuriu.dogapp.views.main.ui.my_dogs

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.adapters.DogAdapter
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.viewModels.BaseViewModel
import com.buzuriu.dogapp.viewModels.DogDetailViewModel
import com.buzuriu.dogapp.views.AccountDetailActivity
import com.buzuriu.dogapp.views.AddDogActivity
import com.buzuriu.dogapp.views.DogDetailActivity
import com.buzuriu.dogapp.views.auth.LoginActivity


@SuppressLint("NotifyDataSetChanged")
class MyDogsViewModel : BaseViewModel() {

    private var dogsList: ArrayList<DogObj> = ArrayList()
    var dogAdapter: DogAdapter?
    var doesUserHaveAnyDog = MutableLiveData<Boolean>(false)

    init {
        val dogsFromLocalDB = localDatabaseService.get<ArrayList<DogObj>>("localDogsList")
        if (dogsFromLocalDB != null && dogsFromLocalDB.size > 0) {
            dogsList.addAll(dogsFromLocalDB)
            doesUserHaveAnyDog.value = true
        }
        else
            doesUserHaveAnyDog.value = false

        dogAdapter = DogAdapter(dogsList, ::selectedDog)
        dogAdapter!!.notifyDataSetChanged()

    }

    override fun onResume() {
        super.onResume()
        val isRefreshListNeeded: Boolean? =
            dataExchangeService.get<Boolean>(this::class.qualifiedName!!)
        val dogsFromLocalDB = localDatabaseService.get<ArrayList<DogObj>>("localDogsList")
        if (isRefreshListNeeded != null && isRefreshListNeeded) {
            dogsList.clear()

            if (dogsFromLocalDB != null && dogsFromLocalDB.size > 0) {
                dogsList.addAll(dogsFromLocalDB)
                doesUserHaveAnyDog.value = true
                dogAdapter!!.notifyDataSetChanged()
            }
            else
                doesUserHaveAnyDog.value = false

        }

    }

    private fun selectedDog(dogObj: DogObj) {
        dataExchangeService.put(DogDetailViewModel::class.java.name, dogObj)
        navigationService.navigateToActivity(DogDetailActivity::class.java, false)
    }

    fun addDog() {
        navigationService.navigateToActivity(AddDogActivity::class.java, false)
    }

    fun goToAccountDetails() {
        navigationService.navigateToActivity(AccountDetailActivity::class.java, false)
    }

    fun logout() {
        firebaseAuthService.logout()
        localDatabaseService.clear()
        navigationService.navigateToActivity(LoginActivity::class.java, true)
    }

}