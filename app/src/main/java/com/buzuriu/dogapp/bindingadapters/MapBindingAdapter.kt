package com.buzuriu.dogapp.bindingadapters

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener

import com.buzuriu.dogapp.components.MapWithPin
import com.buzuriu.dogapp.listeners.IGetLocationListener
import com.google.android.gms.maps.model.LatLng

object MapBindingAdapter {
    @BindingAdapter("cb_getPositionAttrChanged")
    @JvmStatic
    fun MapWithPin.setListener(listener: InverseBindingListener?) {
        if (listener != null) {
            this.getLocationListener =
                (object : IGetLocationListener {
                    override fun getLocation(coords: LatLng?) {
                        mapPosition = coords
                        listener.onChange()
                    }
                })
        }
    }

    @BindingAdapter("cb_getPosition")
    @JvmStatic
    fun MapWithPin.setMyPosition(position: LatLng?) {
        if (position != null) {
            this.mapPosition = position
        }
    }

    @InverseBindingAdapter(attribute = "cb_getPosition")
    @JvmStatic
    fun MapWithPin.getMyPosition(): LatLng? {
        return this.mapPosition
    }
}