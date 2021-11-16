package com.buzuriu.dogapp.viewModels

import com.buzuriu.dogapp.views.auth.RegisterActivity

class MainViewModel : BaseViewModel() {
    var stringTest = "String din view model"
    var string2 = "123456"

    fun showSomething() {
        navigationService.navigateToActivity(RegisterActivity::class.java)
    }
}