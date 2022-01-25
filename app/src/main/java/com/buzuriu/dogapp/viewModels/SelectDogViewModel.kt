package com.buzuriu.dogapp.viewModels

class SelectDogViewModel : BaseViewModel() {

    fun saveDog()
    {}

    fun close() {
        navigationService.closeCurrentActivity()
    }
}