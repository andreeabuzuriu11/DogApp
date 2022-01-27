package com.buzuriu.dogapp.bindingadapters

import android.os.Build
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import java.sql.Time
import java.util.*

object TimePickerBindingAdapter {
    @BindingAdapter(value = ["cb_getTimeAttrChanged"])
    @JvmStatic
    fun TimePicker.setListener(listener: InverseBindingListener?) {
        if (listener != null) {
            this.setOnTimeChangedListener(object : TimePicker.OnTimeChangedListener {
                override fun onTimeChanged(p0: TimePicker?, p1: Int, p2: Int) {
                    listener.onChange()
                }

            })
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    @BindingAdapter("cb_getTime")
    @JvmStatic
    fun TimePicker.setMyTime(calendar: Calendar) {
        if (calendar != null) {
            this.hour = calendar.get(Calendar.HOUR_OF_DAY)
            this.minute = calendar.get(Calendar.MINUTE)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @InverseBindingAdapter(attribute = "cb_getTime")
    @JvmStatic
    fun TimePicker.getMyTime(): Calendar? {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, this.hour)
        calendar.set(Calendar.MINUTE, this.minute)

        return calendar
    }

}