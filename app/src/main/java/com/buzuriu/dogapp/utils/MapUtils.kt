package com.buzuriu.dogapp.utils

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import kotlin.math.cos


class MapUtils {

    companion object {
        fun getLatLngFromGeoPoint(geoPoint: GeoPoint): LatLng {
            val lat: Double = geoPoint.latitude
            val long: Double = geoPoint.longitude
            return LatLng(lat, long)
        }

        private const val metersPerKm = 1000.0

        fun getSouthWestAndNorthEastPointsAroundLocation(
            distance: Int,
            userLocation: LatLng
        ): Pair<GeoPoint, GeoPoint> {
            val userLatitude = userLocation.latitude
            val userLongitude = userLocation.longitude

            val latitudeDegreesFromKm = 1 / 111.0 * distance
            val longitudeDegreesFromKm = 1 / (cos(latitudeDegreesFromKm) * 111.0) * distance

            val southWest = GeoPoint(
                userLatitude - latitudeDegreesFromKm,
                userLongitude - longitudeDegreesFromKm
            )

            val northEast = GeoPoint(
                userLatitude + latitudeDegreesFromKm,
                userLongitude + longitudeDegreesFromKm
            )
            return Pair(southWest, northEast)
        }

        fun getLatLng(lat: Double, lng: Double): LatLng {
            return LatLng(lat, lng)
        }

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