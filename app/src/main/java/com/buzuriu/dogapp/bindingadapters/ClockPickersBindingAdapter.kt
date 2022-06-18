package com.buzuriu.dogapp.bindingadapters

import android.os.Build
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import java.util.*

object ClockPickersBindingAdapter {
    @RequiresApi(Build.VERSION_CODES.O)
    @BindingAdapter(value = ["dateSetBindingAttrChanged"])
    @JvmStatic
    fun DatePicker.setListener(listener: InverseBindingListener?) {
        this.init(
            this.year,
            this.month,
            this.dayOfMonth
        ) { _, _, _, _ -> listener?.onChange() }
    }

    @BindingAdapter("dateSetBinding")
    @JvmStatic
    fun DatePicker.setMyDate(myDate: Calendar) {
        this.updateDate(
            myDate.get(Calendar.YEAR),
            myDate.get(Calendar.MONTH),
            myDate.get(Calendar.DAY_OF_MONTH)
        )
    }

    @InverseBindingAdapter(attribute = "dateSetBinding")
    @JvmStatic
    fun DatePicker.getMyDate(): Calendar? {
        val currentTimeZone = Calendar.getInstance()
        currentTimeZone.set(Calendar.YEAR, this.year)
        currentTimeZone.set(Calendar.MONTH, this.month)
        currentTimeZone.set(Calendar.DAY_OF_MONTH, this.dayOfMonth)

        return currentTimeZone
    }

    @BindingAdapter(value = ["timeSetBindingAttrChanged"])
    @JvmStatic
    fun TimePicker.setListener(listener: InverseBindingListener?) {
        if (listener != null) {
            this.setOnTimeChangedListener { _, _, _ -> listener.onChange() }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @BindingAdapter("timeSetBinding")
    @JvmStatic
    fun TimePicker.setMyTime(myDate: Calendar) {
        // setting my current time initially
        this.hour = myDate.get(Calendar.HOUR_OF_DAY)
        this.minute = myDate.get(Calendar.MINUTE)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @InverseBindingAdapter(attribute = "timeSetBinding")
    @JvmStatic
    fun TimePicker.getMyTime(): Calendar {
        val currentTimeZone = Calendar.getInstance()
        currentTimeZone.set(Calendar.HOUR_OF_DAY, this.hour)
        currentTimeZone.set(Calendar.MINUTE, this.minute)

        return currentTimeZone
    }


}