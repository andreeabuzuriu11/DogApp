package com.buzuriu.dogapp.components

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.listeners.IGetLocationListener
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng


class MapWithPin : FrameLayout, OnMapReadyCallback, DefaultLifecycleObserver {

    var mMap: GoogleMap? = null
    private var locationManager: LocationManager? = null
    private var mapBundle: Bundle? = null
    private var mapView: MapView?
    var mapPosition: LatLng? = null
    var getLocationListener: IGetLocationListener? = null

    constructor(var1: Context) : super(var1)
    constructor(var1: Context, var2: AttributeSet) : super(var1, var2)
    constructor(var1: Context, var2: AttributeSet, var3: Int) : super(var1, var2, var3)

    init {
        addView(inflate(context, R.layout.custom_pin_map, null))
        mapView = this.findViewById(R.id.mapWithPin)
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap!!.isMyLocationEnabled = true
        mMap!!.uiSettings.isMyLocationButtonEnabled = true

        locationManager =
            context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        updateMyLocation()
        initLocationListener()
    }

    fun setCameraPosition(coords: LatLng, zoom: Float? = null) {

        var cameraUpdate: CameraUpdate?
        if (zoom != null) {
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(coords, zoom)
        } else {
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(coords, 10f)
        }

        mMap?.animateCamera(cameraUpdate)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        return super.dispatchTouchEvent(ev)
    }

    @SuppressLint("MissingPermission")
    fun updateMyLocation() {
        if (mapPosition==null) {
            val locationListener = object : LocationListener {
                @TargetApi(Build.VERSION_CODES.M)
                override fun onLocationChanged(p0: Location) {

                    mapPosition = LatLng(p0.latitude, p0.longitude)
                    setCameraPosition(mapPosition!!)
                    locationManager!!.removeUpdates(this)
                    //now map is ready to show current position
                }

                override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
                }

            }

            locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 0,
                0F, locationListener
            )

            locationManager!!.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0,
                0f,
                locationListener
            )
        } else {
            setCameraPosition(mapPosition!!)
        }

    }

    fun initLocationListener() {
        mMap?.setOnCameraIdleListener {
            getLocationListener?.getLocation(mMap?.cameraPosition?.target)
        }
    }

    fun getMapAsync() {
        mapView?.getMapAsync(this)
    }

    fun setMapBundle(bundle: Bundle?) {
        mapBundle = bundle

    }

    override fun onCreate(lifecycleOwner: LifecycleOwner) {
        mapView?.onCreate(mapBundle)
    }

    override fun onResume(lifecycleOwner: LifecycleOwner) {
        mapView?.onResume()
    }

    override fun onStart(lifecycleOwner: LifecycleOwner) {
        mapView?.onStart()
    }

    override fun onStop(lifecycleOwner: LifecycleOwner) {
        mapView?.onStop()
    }

    override fun onPause(lifecycleOwner: LifecycleOwner) {
        mapView?.onPause()
    }

    override fun onDestroy(lifecycleOwner: LifecycleOwner) {
        mapView?.onDestroy()
    }

}