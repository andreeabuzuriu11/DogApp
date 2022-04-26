package com.buzuriu.dogapp.views

import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.components.MapWithPin
import com.buzuriu.dogapp.databinding.ActivityAddMeetingBinding
import com.buzuriu.dogapp.viewModels.AddMeetingViewModel
import com.buzuriu.dogapp.views.base.BaseBoundActivity

class AddMeetingActivity :
    BaseBoundActivity<AddMeetingViewModel, ActivityAddMeetingBinding>(AddMeetingViewModel::class.java) {


    override val layoutId: Int = R.layout.activity_add_meeting

    private val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"
    var mapView: MapWithPin? = null

    override fun setupDataBinding(binding: ActivityAddMeetingBinding) {
        binding.viewModel = mViewModel
        restrictDatePicker()
        set24HourFormat()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var mapViewBundle: Bundle? = null

        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
        }


        mapView = findViewById(R.id.map_with_pin)
        mapView!!.setMapBundle(mapViewBundle)
        mapView!!.getMapAsync()
        lifecycle.addObserver(mapView!!)
    }

    private fun restrictDatePicker() {
        var datePicker: DatePicker = this.findViewById(R.id.date_picker)
        datePicker.minDate = System.currentTimeMillis() - 1000
    }

    private fun set24HourFormat() {
        var timePicker: TimePicker = this.findViewById(R.id.timePicker)
        timePicker.setIs24HourView(true)
    }


}