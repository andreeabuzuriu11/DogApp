package com.buzuriu.dogapp.bindingadapters

import android.os.Build
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import java.util.*

object DatePickerBindingAdapter {
    @RequiresApi(Build.VERSION_CODES.O)
    @BindingAdapter(value = ["cb_getDateAttrChanged"])
    @JvmStatic
    fun DatePicker.setListener(listener: InverseBindingListener?) {
        this.init(
            this.year,
            this.month,
            this.dayOfMonth,
            object : DatePicker.OnDateChangedListener {
                override fun onDateChanged(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
                    listener?.onChange()
                }
            })
    }

    @BindingAdapter("cb_getDate")
    @JvmStatic
    fun DatePicker.setMyDate(calendar: Calendar) {
        if (calendar != null) {
            this.updateDate(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        }
    }

    @InverseBindingAdapter(attribute = "cb_getDate")
    @JvmStatic
    fun DatePicker.getMyDate(): Calendar? {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, this.year)
        calendar.set(Calendar.MONTH, this.month)
        calendar.set(Calendar.DAY_OF_MONTH, this.dayOfMonth)

        return calendar
    }

}