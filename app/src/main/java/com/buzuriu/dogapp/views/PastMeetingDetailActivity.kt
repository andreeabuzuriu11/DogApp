package com.buzuriu.dogapp.views

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.ActivityPastMeetingDetailBinding
import com.buzuriu.dogapp.viewModels.PastMeetingDetailViewModel
import com.buzuriu.dogapp.views.base.BaseBoundActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class PastMeetingDetailActivity : BaseBoundActivity<
        PastMeetingDetailViewModel, ActivityPastMeetingDetailBinding>(PastMeetingDetailViewModel::class.java), OnMapReadyCallback{

    override val layoutId: Int
        get() = R.layout.activity_past_meeting_detail

    override fun setupDataBinding(binding: ActivityPastMeetingDetailBinding) {
        binding.viewModel = mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initParticipantList()

        val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.map, mapFragment)
            .commit()

        mapFragment.getMapAsync(this)
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

    private fun initParticipantList() {
        val partList = findViewById<RecyclerView>(R.id.participants_list)
        partList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        partList.adapter = mViewModel.participantsAdapter
    }

}