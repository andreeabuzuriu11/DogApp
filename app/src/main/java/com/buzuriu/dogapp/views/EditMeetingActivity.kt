package com.buzuriu.dogapp.views

import android.os.Bundle
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.components.MapWithPin
import com.buzuriu.dogapp.databinding.ActivityEditMeetingBinding
import com.buzuriu.dogapp.utils.LocalDBItems
import com.buzuriu.dogapp.viewModels.EditMeetingViewModel
import com.buzuriu.dogapp.views.base.BaseBoundActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class EditMeetingActivity : BaseBoundActivity<EditMeetingViewModel, ActivityEditMeetingBinding>(
    EditMeetingViewModel::class.java
), OnMapReadyCallback {
    override val layoutId: Int
        get() = R.layout.activity_edit_meeting

    var mapView: MapWithPin? = null

    override fun setupDataBinding(binding: ActivityEditMeetingBinding) {
        binding.viewModel = mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var mapViewBundle: Bundle? = null

        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(LocalDBItems.mapBundle)
        }

        mapView = findViewById(R.id.map_with_pin)
        mapView!!.setMapBundle(mapViewBundle)
        mapView!!.getMapAsync()
        lifecycle.addObserver(mapView!!)
    }

    override fun onMapReady(p0: GoogleMap) {
        val coords =
            LatLng(mViewModel.myLatLng.value!!.latitude, mViewModel.myLatLng.value!!.longitude)
        p0.addMarker(
            MarkerOptions()
                .position(coords)
                .title("This is the meeting point")
        )

        p0.setMinZoomPreference(10.0f)
        p0.setMaxZoomPreference(14.0f)
        p0.moveCamera(CameraUpdateFactory.newLatLng(coords))
    }
}