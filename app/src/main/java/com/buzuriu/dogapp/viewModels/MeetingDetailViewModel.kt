package com.buzuriu.dogapp.viewModels

import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import com.buzuriu.dogapp.models.UserInfo

class MeetingDetailViewModel :BaseViewModel(){

    var myCustomMeetingObj = MutableLiveData<MyCustomMeetingObj>()

    init {
        myCustomMeetingObj.value = dataExchangeService.get<MyCustomMeetingObj>(this::class.java.name)
    }

}