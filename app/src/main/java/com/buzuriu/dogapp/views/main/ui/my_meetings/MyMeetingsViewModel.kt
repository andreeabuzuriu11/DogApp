package com.buzuriu.dogapp.views.main.ui.my_meetings

import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.adapters.MyMeetingAdapter
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import com.buzuriu.dogapp.viewModels.BaseViewModel
import com.buzuriu.dogapp.viewModels.MyMeetingDetailViewModel
import com.buzuriu.dogapp.views.AddMeetingActivity
import com.buzuriu.dogapp.views.MyMeetingDetailActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MyMeetingsViewModel : BaseViewModel() {
    var meetingsList = ArrayList<MyCustomMeetingObj>()
    var meetingAdapter: MyMeetingAdapter?

    init {
        val meetingsFromLocalDB = localDatabaseService.get<ArrayList<MyCustomMeetingObj>>("localMeetingsList")
        if (meetingsFromLocalDB != null) {
            meetingsList.addAll(meetingsFromLocalDB)
        }

        meetingAdapter = MyMeetingAdapter(meetingsList, ::selectedMeeting)
        meetingAdapter!!.notifyDataSetChanged()
    }

    private fun selectedMeeting(meeting: MyCustomMeetingObj)
    {
        dataExchangeService.put(MyMeetingDetailViewModel::class.java.name, meeting)
        navigationService.navigateToActivity(MyMeetingDetailActivity::class.java, false)
    }

    fun addMeeting()
    {
        if (!doesUserHaveAtLeastOneDog())
        {
            dialogService.showSnackbar("Please add your pet before participating to a meeting")
            return
        }

        viewModelScope.launch(Dispatchers.Main) {
            val hasPermission = askLocationPermission().await()
            if (!hasPermission) {
                dialogService.showSnackbar("Location permission needed to add a meeting!")
                return@launch
            }
            else {
                navigationService.navigateToActivity(AddMeetingActivity::class.java, false)
            }
        }
    }

    private fun doesUserHaveAtLeastOneDog() : Boolean
    {
        if (localDatabaseService.get<ArrayList<DogObj>>("localDogsList")!!.size < 1)
            return false
        return true
    }
}