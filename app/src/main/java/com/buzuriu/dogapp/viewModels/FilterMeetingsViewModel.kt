package com.buzuriu.dogapp.viewModels
class FilterMeetingsViewModel : BaseViewModel(){

    fun close()
    {
        navigationService.closeCurrentActivity()
    }
}