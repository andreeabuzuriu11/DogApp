package com.buzuriu.dogapp.viewModels

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.models.BreedObj
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.utils.ImageUtils
import com.buzuriu.dogapp.views.SelectDogFragment
import com.buzuriu.dogapp.views.main.ui.OverlayActivity
import com.google.android.gms.maps.model.LatLng
import java.util.*

class AddMeetingViewModel : BaseViewModel() {

    var dog = MutableLiveData<DogObj>()
    var dogPlaceHolder: MutableLiveData<Drawable>
    var datePickerCalendar = MutableLiveData<Calendar>()
    var timePickerCalendar = MutableLiveData<Calendar>()
    var position = MutableLiveData<LatLng>()

    init {
        dogPlaceHolder = MutableLiveData<Drawable>(getDogPlaceHolder())
        datePickerCalendar.value = Calendar.getInstance()
        timePickerCalendar.value = Calendar.getInstance()
    }

    fun selectDog() {
        navigationService.showOverlay(
            OverlayActivity::class.java,
            false,
            OverlayActivity.fragmentClassNameParam,
            SelectDogFragment::class.qualifiedName
        )
        if (!dog.value?.name.isNullOrEmpty())
            dataExchangeService.put(
                SelectDogViewModel::class.qualifiedName!!,
                dog.value?.name.toString()
            )
    }

    private fun getDateAndTimeOfMeeting()
    {
        val year = datePickerCalendar.value!!.get(Calendar.YEAR)
        val month = datePickerCalendar.value!!.get(Calendar.MONTH)
        val day = datePickerCalendar.value!!.get(Calendar.DAY_OF_MONTH)
        val hour = timePickerCalendar.value!!.get(Calendar.HOUR_OF_DAY)
        val minute = timePickerCalendar.value!!.get(Calendar.MINUTE)
    }

    private fun getCoordinate()
    {
        val long = position.value!!.longitude
        val lat = position.value!!.latitude
    }

    fun createMeeting()
    {
        getDateAndTimeOfMeeting()
        getCoordinate()
    }

    override fun onResume() {
        val selectedDog = dataExchangeService.get<DogObj>(this::class.qualifiedName!!)
        if (selectedDog != null) {
            dog.value = selectedDog!!
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun getDogPlaceHolder(): Drawable? {
        return activityService.activity!!.getDrawable(R.drawable.ic_dog_svgrepo_com)
    }
}