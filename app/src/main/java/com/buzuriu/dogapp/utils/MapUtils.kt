package com.buzuriu.dogapp.utils

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import kotlin.math.cos

class MapUtils {

    companion object {
        fun getLatLngFromGeoPoint(geoPoint: GeoPoint): LatLng {
            var lat: Double = geoPoint.latitude
            var long: Double = geoPoint.longitude
            return LatLng(lat, long)
        }

        private const val metersPerKm = 1000.0
        private const val kmInDegreesLat = 1 / 110.574
        private val kmInDegreeLong = 1 / (111.320) * cos(kmInDegreesLat)


        fun getGreaterPoint(radiusInKm: Int, centerPoint: LatLng): GeoPoint {
            return GeoPoint(
                centerPoint.latitude + (radiusInKm * kmInDegreesLat),
                centerPoint.longitude + (radiusInKm * kmInDegreeLong)
            )
        }

        fun getLesserPoint(radiusInKm: Int, centerPoint: LatLng): GeoPoint {
            return GeoPoint(
                centerPoint.latitude - (radiusInKm * kmInDegreesLat),
                centerPoint.longitude - (radiusInKm * kmInDegreeLong)
            )
        }

        fun getLatLng(lat: Double, lng: Double): LatLng {
            return LatLng(lat, lng)
        }

        private const val metersPerMile = 1609.44

        fun getDistanceBetweenCoords(
            coords1: LatLng,
            coords2: LatLng
        ): Double {
            val location1 = Location("1")
            val location2 = Location("2")

            location1.latitude = coords1.latitude
            location1.longitude = coords1.longitude

            location2.latitude = coords2.latitude
            location2.longitude = coords2.longitude

            return location1.distanceTo(location2) / metersPerKm

        }
    }
}