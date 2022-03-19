package com.buzuriu.dogapp.views.main.ui.dashboard

import android.telecom.Call
import com.buzuriu.dogapp.adapters.DogAdapter
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.viewModels.AccountDetailViewModel
import com.buzuriu.dogapp.viewModels.BaseViewModel
import com.buzuriu.dogapp.viewModels.DogDetailViewModel
import com.buzuriu.dogapp.views.AccountDetailActivity
import com.buzuriu.dogapp.views.AddDogActivity
import com.buzuriu.dogapp.views.DogDetailActivity
import com.buzuriu.dogapp.views.auth.LoginActivity


class DashboardViewModel : BaseViewModel() {

    var dogsList: ArrayList<DogObj> = ArrayList()
    var dogAdapter: DogAdapter?

    init {
        val dogsFromLocalDB = localDatabaseService.get<ArrayList<DogObj>>("localDogsList")
        if (dogsFromLocalDB != null) {
            dogsList.addAll(dogsFromLocalDB)
        }
        dogAdapter = DogAdapter(dogsList, ::selectedDog)
        dogAdapter!!.notifyDataSetChanged()

    }

    override fun onResume() {
        super.onResume()
        var isRefreshListNeeded: Boolean? =
            dataExchangeService.get<Boolean>(this::class.qualifiedName!!)
        var dogsFromLocalDB = localDatabaseService.get<ArrayList<DogObj>>("localDogsList")
        if (isRefreshListNeeded != null && isRefreshListNeeded) {
            dogsList.clear()

            if (dogsFromLocalDB != null) {
                dogsList.addAll(dogsFromLocalDB)
                dogAdapter!!.notifyDataSetChanged()
            }
        }

    }


    private fun selectedDog(dogObj: DogObj) {
        dataExchangeService.put(DogDetailViewModel::class.java.name, dogObj)
        navigationService.navigateToActivity(DogDetailActivity::class.java, false)
    }

    fun addDog() {
        navigationService.navigateToActivity(AddDogActivity::class.java, false)
    }

    fun goToAccountDetails()
    {
        navigationService.navigateToActivity(AccountDetailActivity::class.java, false)
    }

    fun logout() {
        firebaseAuthService.logout()
        localDatabaseService.clear()
        navigationService.navigateToActivity(LoginActivity::class.java, true)
    }

}