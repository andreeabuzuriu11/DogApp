package com.buzuriu.dogapp.listeners

import com.google.android.gms.maps.model.LatLng

interface IGetLocationListener {
    fun getLocation(coords: LatLng?)
}