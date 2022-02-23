package com.buzuriu.dogapp.utils

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint

class MapUtils {

    companion object
    {
       fun getLatLngFromGeoPoint (geoPoint: GeoPoint) : LatLng
       {
           var lat: Double = geoPoint.latitude
           var long: Double = geoPoint.longitude
           return LatLng(lat, long)
       }
    }
}