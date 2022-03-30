package com.buzuriu.dogapp.views

import android.os.Bundle
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.FragmentMeetingsOnMapBinding
import com.buzuriu.dogapp.viewModels.MeetingsOnMapViewModel
import com.buzuriu.dogapp.views.base.BaseBoundActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MeetingsOnMapActivity : BaseBoundActivity<MeetingsOnMapViewModel, FragmentMeetingsOnMapBinding>(
    MeetingsOnMapViewModel::class.java) , OnMapReadyCallback{

    override val layoutId: Int
        get() = R.layout.fragment_meetings_on_map

    override fun setupDataBinding(binding: FragmentMeetingsOnMapBinding) {
        binding.viewModel = mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.map, mapFragment)
            .commit()

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap) {
      /*  for (point in mViewModel.locationMeetings)
        {
            p0.addMarker(
                MarkerOptions()
                    .position(point)
                    .title("Meeting Point")
            )
        }*/

    }

}