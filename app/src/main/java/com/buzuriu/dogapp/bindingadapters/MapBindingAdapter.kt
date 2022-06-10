package com.buzuriu.dogapp.bindingadapters

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener

import com.buzuriu.dogapp.components.MapWithPin
import com.buzuriu.dogapp.listeners.IGetLocationListener
import com.google.android.gms.maps.model.LatLng

object MapBindingAdapter {
    @BindingAdapter("latLngMapBindingAttrChanged")
    @JvmStatic
    fun MapWithPin.setListener(listener: InverseBindingListener?) {
        if (listener != null) {
            this.getLocationListener =
                (object : IGetLocationListener {
                    override fun getLocation(latLng: LatLng?) {
                        mapPosition = latLng
                        listener.onChange()
                    }
                })
        }
    }

    @BindingAdapter("latLngMapBinding")
    @JvmStatic
    fun MapWithPin.setMyPosition(position: LatLng?) {
        if (position != null) {
            this.mapPosition = position
        }
    }

    @InverseBindingAdapter(attribute = "latLngMapBinding")
    @JvmStatic
    fun MapWithPin.getMyPosition(): LatLng? {
        return this.mapPosition
    }
}