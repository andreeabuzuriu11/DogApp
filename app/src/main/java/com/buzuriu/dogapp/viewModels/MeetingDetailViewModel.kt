package com.buzuriu.dogapp.viewModels

import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import com.buzuriu.dogapp.models.UserInfo

class MeetingDetailViewModel :BaseViewModel(){
 /*   var dog = MutableLiveData<DogObj>()*/
    var user = MutableLiveData<UserInfo>()
    var myCustomMeetingObj = MutableLiveData<MyCustomMeetingObj>()

    init {
   /*     dog.value = dataExchangeService.get<DogObj>(this::class.java.name)
        user.value = dataExchangeService.get<UserInfo>(this::class.java.name)*/
        myCustomMeetingObj.value = dataExchangeService.get<MyCustomMeetingObj>(this::class.java.name)
    }

}