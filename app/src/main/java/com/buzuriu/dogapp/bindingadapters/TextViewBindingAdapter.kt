package com.buzuriu.dogapp.bindingadapters

import android.annotation.TargetApi
import android.os.Build
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.buzuriu.dogapp.utils.DateUtils

object TextViewBindingAdapter {
    @TargetApi(Build.VERSION_CODES.M)
    @BindingAdapter("cb_meetingDate")
    @JvmStatic
    fun TextView.setMeetingDateText(date: Long) {
        this.text = DateUtils.getDateString(date)
    }

    @TargetApi(Build.VERSION_CODES.M)
    @BindingAdapter("cb_meetingTime")
    @JvmStatic
    fun TextView.setMeetingTimeText(date: Long) {
        this.text = DateUtils.getTimeString(date)
    }
}