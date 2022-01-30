package com.buzuriu.dogapp.views.main.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.adapters.DogAdapter
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.models.MeetingObj
import com.buzuriu.dogapp.viewModels.BaseViewModel
import com.buzuriu.dogapp.views.AddMeetingActivity
import com.buzuriu.dogapp.views.SelectBreedFragment
import com.buzuriu.dogapp.views.main.ui.OverlayActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MapViewModel : BaseViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is map Fragment"
    }
    val text: LiveData<String> = _text
    var meetingsList = ArrayList<MeetingObj>()

    init {
        val meetingFromLocalDB = localDatabaseService.get<ArrayList<MeetingObj>>("localMeetings")
        if (meetingFromLocalDB != null) {
            meetingsList.addAll(meetingFromLocalDB)
        }

    }

    fun showMap() {
        viewModelScope.launch(Dispatchers.Main) {

            val hasPermission = askLocationPermission().await()
            if (!hasPermission) {
                dialogService.showSnackbar("Location permission needed")
                return@launch
            }
        }
    }

    fun addMeeting()
    {
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

}