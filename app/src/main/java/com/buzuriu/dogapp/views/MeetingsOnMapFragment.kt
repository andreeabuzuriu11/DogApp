package com.buzuriu.dogapp.views

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.FragmentMeetingsOnMapBinding
import com.buzuriu.dogapp.viewModels.MeetingsOnMapViewModel
import com.buzuriu.dogapp.views.base.BaseBoundFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MeetingsOnMapFragment :
    BaseBoundFragment<MeetingsOnMapViewModel, FragmentMeetingsOnMapBinding>(
        MeetingsOnMapViewModel::class.java
    ), OnMapReadyCallback {

    private lateinit var locationListener: LocationListener
    private var locationManager: LocationManager? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var googleMap: GoogleMap? = null
    var circle: Circle? = null

    override val layoutId: Int
        get() = R.layout.fragment_meetings_on_map

    override fun setupDataBinding(binding: FragmentMeetingsOnMapBinding) {
        binding.viewModel = mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mapFragment = SupportMapFragment.newInstance()
        parentFragmentManager
            .beginTransaction()
            .add(R.id.map, mapFragment)
            .commit()

        mapFragment.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        for (point in mViewModel.locationMeetings!!) {
            googleMap!!.addMarker(
                MarkerOptions()
                    .position(point)
                    .title("Meeting Point")
            )
        }

        locationManager =
            context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        googleMap!!.isMyLocationEnabled = true
        googleMap!!.uiSettings.isMyLocationButtonEnabled = true

        getLocation()

        try {
            mViewModel.progress.observe(requireActivity(), Observer {
                drawCircle(LatLng(latitude, longitude), it.toDouble() * 1000)
            })
        } catch (e: java.lang.Exception) {
            Log.d("Error", "Something went wrong: " + e.message)
        }
    }

    private fun getLocation() {
        locationListener = LocationListener { location ->
            latitude = location.latitude
            longitude = location.longitude
            mViewModel.userCoordinates.value = LatLng(latitude, longitude)
        }

        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_ACCESS_FINE_LOCATION
            )
            return
        }
        locationManager!!.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0L,
            0f,
            locationListener
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
            when (grantResults[0]) {
                PackageManager.PERMISSION_GRANTED -> getLocation()
                PackageManager.PERMISSION_DENIED -> {
                }//Tell to user the need of grant permission
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100
    }

    private fun drawCircle(point: LatLng, radius: Double) {

        // Instantiating CircleOptions to draw a circle around the marker
        val circleOptions = CircleOptions()
        // Specifying the center of the circle
        circleOptions.center(point)
        // Radius of the circle
        circleOptions.radius(radius)
        // Border color of the circle
        circleOptions.strokeColor(0x220000FF)
        // Fill color of the circle
        circleOptions.fillColor(0x220000FF)
        // Border width of the circle
        circleOptions.strokeWidth(2f)
        // Adding the circle to the GoogleMap
        if (circle != null) {
            circle!!.remove()
        }

        circle = googleMap!!.addCircle(circleOptions)
    }


}