package com.buzuriu.dogapp.views.main.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.viewModels.BaseViewModel
import com.buzuriu.dogapp.views.AddDogActivity
import com.buzuriu.dogapp.views.AddMeetingActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MapViewModel : BaseViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is map Fragment"
    }
    val text: LiveData<String> = _text

    fun showMap() {
        viewModelScope.launch(Dispatchers.Main) {

            val hasPermission = askLocationPermission().await()
            if (!hasPermission) {
                dialogService.showSnackbar("Location permission needed")
                return@launch
            }
            else {

            }
        }
    }

    fun addMeeting()
    {
        navigationService.navigateToActivity(AddMeetingActivity::class.java, false)
    }
}