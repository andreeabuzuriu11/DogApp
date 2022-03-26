package com.buzuriu.dogapp.views

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.ActivityMyMeetingDetailBinding
import com.buzuriu.dogapp.viewModels.MyMeetingDetailViewModel
import com.buzuriu.dogapp.views.base.BaseBoundActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MyMeetingDetailActivity : BaseBoundActivity<MyMeetingDetailViewModel, ActivityMyMeetingDetailBinding>(
    MyMeetingDetailViewModel::class.java), OnMapReadyCallback {

    override val layoutId: Int
        get() = R.layout.activity_my_meeting_detail

    override fun setupDataBinding(binding: ActivityMyMeetingDetailBinding) {
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.getItemId()
        if (id == R.id.edit) {
            mViewModel.editMeeting()
            return true
        }
        if(id == R.id.delete)
        {
            mViewModel.deleteMeeting()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMapReady(p0: GoogleMap) {
        val coords = LatLng(mViewModel.myLatLng.value!!.latitude, mViewModel.myLatLng.value!!.longitude)
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